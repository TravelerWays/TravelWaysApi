package travel.way.travelwayapi.auth.filters;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import travel.way.travelwayapi.auth.service.internal.JwtUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {
    private static final List<String> publicUri = List.of("/login", "/refresh", "/register");
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(shouldAuthorization(request.getServletPath())){
            try {
                var token = request.getHeader(HttpHeaders.AUTHORIZATION);
                if(token == null  || token.startsWith(JwtUtils.TOKEN_TYPE)) {
                    token = token.substring(JwtUtils.TOKEN_TYPE.length()).trim();

                    jwtUtils.authenticationUser(token);
                }
            }catch (Exception e){
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean shouldAuthorization(String requestPath){
        return !publicUri.contains(requestPath);
    }
}
