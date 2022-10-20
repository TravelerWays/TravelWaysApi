package travel.way.travelwayapi.auth.controllers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.way.travelwayapi.auth.models.dto.response.AuthResponse;
import travel.way.travelwayapi.auth.service.internal.JwtUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final JwtUtils jwtUtils;

    @SneakyThrows
    @PostMapping("/refresh")
    public AuthResponse refresh(@CookieValue("refreshToken") String refreshToken, HttpServletRequest request, HttpServletResponse response) {
        var newRefreshToken = jwtUtils.refreshToken(refreshToken);
        var jwt = jwtUtils.generateJwt(jwtUtils.getUserFromRefreshToken(newRefreshToken));

        var cookie = new Cookie("refreshToken", newRefreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);

        return new AuthResponse(jwt);
    }
}
