package travel.ways.travelwaysapi.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import travel.ways.travelwaysapi._core.model.dto.BaseErrorResponse;
import travel.ways.travelwaysapi._core.util.Time;
import travel.ways.travelwaysapi.auth.model.CustomUserDetails;
import travel.ways.travelwaysapi.auth.model.dto.request.LoginForm;
import travel.ways.travelwaysapi.auth.model.dto.response.AuthResponse;
import travel.ways.travelwaysapi.auth.service.internal.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final Time time;

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        if (CorsUtils.isPreFlightRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(null, null);
            return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }

        var form = new ObjectMapper().readValue(request.getInputStream(), LoginForm.class);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword());

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    @SneakyThrows
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {

        var user = (CustomUserDetails) authResult.getPrincipal();

        var jwt = jwtService.generateJwt(user.getUsername());
        var refreshToken = jwtService.generateRefreshToken(user.getUsername());

        var cookie = jwtService.getRefreshCookie(refreshToken);
        response.addCookie(cookie);

        response.setContentType(APPLICATION_JSON_VALUE);

        var authResponse = new AuthResponse(jwt);

        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    @Override
    @SneakyThrows
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        var baseError = new BaseErrorResponse(failed.getMessage(), HttpStatus.UNAUTHORIZED, time.now().getTimestamp());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        new ObjectMapper().writeValue(response.getOutputStream(), baseError);
    }
}
