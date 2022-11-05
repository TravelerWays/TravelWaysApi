package travel.ways.travelwaysapi.auth.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import travel.ways.travelwaysapi.auth.service.internal.JwtService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
    private static final List<String> publicUri = List.of("/api/auth", "/api/account/password-recovery");
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (shouldAuthorization(request.getServletPath())) {
            try {
                var token = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (token == null || token.startsWith(JwtService.TOKEN_TYPE)) {
                    token = token.substring(JwtService.TOKEN_TYPE.length()).trim();

                    jwtService.authenticationUser(token);
                }
            } catch (Exception e) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean shouldAuthorization(String requestPath) {
        return publicUri.stream().noneMatch(requestPath::startsWith);
    }
}
