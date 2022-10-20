package travel.way.travelwayapi.auth.service.impl;

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
import travel.way.travelwayapi._core.exceptions.ServerException;
import travel.way.travelwayapi._core.properites.AuthProperties;
import travel.way.travelwayapi.auth.models.db.RefreshToken;
import travel.way.travelwayapi.auth.reposiotry.RefreshTokenRepository;
import travel.way.travelwayapi.auth.service.internal.JwtUtils;
import travel.way.travelwayapi.user.models.db.Role;
import travel.way.travelwayapi.user.shared.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class JwtUtilsImpl implements JwtUtils {
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthProperties authProperties;
    private final static String ROLES_CLAIMS = "roles";

    @Override
    public String generateJwt(String username) {
        Algorithm algorithm = Algorithm.HMAC256(authProperties.getSecret().getBytes());
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
        if(token.isUsed()){
            revokeAllConnectedToken(token);
            throw new ServerException("Invalid refresh token", HttpStatus.FORBIDDEN.value());
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

    private String getRandomString(){
        var alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        var stringBuilder = new StringBuilder(10);
        var random = new Random();

        for(int i = 0; i < 10; i++){
          stringBuilder.append(alphaNumericString.charAt(random.nextInt(alphaNumericString.length())));
        }

        return stringBuilder.toString();
    }

    private DecodedJWT verifierJwt(String jwt) throws JWTVerificationException {
        var algorithm = Algorithm.HMAC256(authProperties.getSecret().getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(jwt);
    }

    private void revokeAllConnectedToken(RefreshToken token){
        refreshTokenRepository.revokeRefresh(token.getUser());
    }
}
