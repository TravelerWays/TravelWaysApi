package travel.ways.travelwaysapi.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.properity.AuthProperty;
import travel.ways.travelwaysapi.auth.model.db.RefreshToken;
import travel.ways.travelwaysapi.auth.repository.RefreshTokenRepository;
import travel.ways.travelwaysapi.auth.service.internal.JwtService;
import travel.ways.travelwaysapi.user.model.db.Role;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.servlet.http.Cookie;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthProperty authProperty;

    private final static String ROLES_CLAIMS = "roles";
    private final static String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    @Override
    public String generateJwt(String username) {
        Algorithm algorithm = Algorithm.HMAC256(authProperty.getSecret().getBytes());
        var user = userService.getByUsername(username);
        return JWT
                .create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                .withClaim(ROLES_CLAIMS, user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .sign(algorithm);
    }

    @Override
    public String getUserFromRefreshToken(String refreshToken) {
        var token = refreshTokenRepository.findByToken(refreshToken);

        return token.getUser().getUsername();
    }

    @Override
    @SneakyThrows
    @Transactional
    public String refreshToken(String refreshToken) {
        var token = refreshTokenRepository.findByToken(refreshToken);
        if (token.isUsed()) {
            revokeAllConnectedToken(token);
            throw new ServerException("Invalid refresh token", HttpStatus.FORBIDDEN);
        }

        token.setUsed(true);
        return generateRefreshToken(token.getUser().getUsername());
    }

    @Override
    @Transactional
    public String generateRefreshToken(String username) {
        var token = getRandomString();
        var user = userService.getByUsername(username);

        var refreshToken = new RefreshToken(
                token,
                false,
                user
        );

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    public void authenticationUser(String jwt) throws JWTVerificationException {
        var decodedJwt = verifierJwt(jwt);
        var username = decodedJwt.getSubject();
        var roles = decodedJwt.getClaim(ROLES_CLAIMS).asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
        });

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Override
    public Cookie getRefreshCookie(String refreshToken) {
        var cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    private String getRandomString() {
        var alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        var stringBuilder = new StringBuilder(10);
        var secureRandom = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            stringBuilder.append(alphaNumericString.charAt(secureRandom.nextInt(alphaNumericString.length())));
        }

        return stringBuilder.toString();
    }

    private DecodedJWT verifierJwt(String jwt) throws JWTVerificationException {
        var algorithm = Algorithm.HMAC256(authProperty.getSecret().getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(jwt);
    }

    private void revokeAllConnectedToken(RefreshToken token) {
        refreshTokenRepository.revokeRefresh(token.getUser());
    }
}
