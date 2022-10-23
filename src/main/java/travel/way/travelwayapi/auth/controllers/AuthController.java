package travel.way.travelwayapi.auth.controllers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.way.travelwayapi.auth.models.dto.response.AuthResponse;
import travel.way.travelwayapi.auth.services.internal.JwtService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtService jwtService;

    @SneakyThrows
    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue("refreshToken") String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        var newRefreshToken = jwtService.refreshToken(refreshToken);
        var jwt = jwtService.generateJwt(jwtService.getUserFromRefreshToken(newRefreshToken));

        response.addCookie(jwtService.getRefreshCookie(newRefreshToken));

        return new AuthResponse(jwt);
    }
}
