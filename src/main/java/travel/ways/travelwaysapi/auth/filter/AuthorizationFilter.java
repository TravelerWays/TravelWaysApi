package travel.ways.travelwaysapi.auth.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import travel.ways.travelwaysapi._core.config.SecurityConfig;
import travel.ways.travelwaysapi._core.model.dto.BaseErrorResponse;
import travel.ways.travelwaysapi._core.util.Time;
import travel.ways.travelwaysapi.auth.service.internal.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final Time time;

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        var token = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            var isPublicApiRequest = SecurityConfig.PublicURI.stream().noneMatch(x -> request.getRequestURI().startsWith(x.replace("**", "")));
            if (!isPublicApiRequest) {
                token = token.substring(JwtService.TOKEN_TYPE.length()).trim();
                jwtService.authenticateUser(token);
            }
        } catch (JWTDecodeException | TokenExpiredException e) {
            response.setContentType(APPLICATION_JSON_VALUE);
            BaseErrorResponse baseError = new BaseErrorResponse("JWT error", HttpStatus.FORBIDDEN, time.now().getTimestamp());
            response.setStatus(baseError.getStatus().value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(baseError));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
