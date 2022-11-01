package travel.ways.travelwaysapi.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import travel.ways.travelwaysapi.auth.model.dto.request.LoginForm;
import travel.ways.travelwaysapi.auth.model.dto.response.AuthResponse;
import travel.ways.travelwaysapi.auth.service.internal.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (CorsUtils.isPreFlightRequest(request)) {
            response.setStatus(HttpServletResponse.SC_OK);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(null, null);
            return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }

        ObjectMapper mapper = new ObjectMapper();
        var form = mapper.readValue(request.getInputStream(), LoginForm.class);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword());

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        var user = (User) authResult.getPrincipal();

        var jwt = jwtService.generateJwt(user.getUsername());
        var refreshToken = jwtService.generateRefreshToken(user.getUsername());

        var cookie = jwtService.getRefreshCookie(refreshToken);
        response.addCookie(cookie);

        response.setContentType(APPLICATION_JSON_VALUE);

        var authResponse = new AuthResponse(jwt);

        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }
}
