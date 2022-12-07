package travel.ways.travelwaysapi.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.ways.travelwaysapi.auth.model.dto.response.AuthResponse;
import travel.ways.travelwaysapi.auth.service.internal.JwtService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;

    @SneakyThrows
    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        var newRefreshToken = jwtService.refreshToken(refreshToken);
        var jwt = jwtService.generateJwt(jwtService.getUserFromRefreshToken(newRefreshToken));

        response.addCookie(jwtService.getRefreshCookie(newRefreshToken));

        return new AuthResponse(jwt);
    }
}
