package travel.ways.travelwaysapi.auth.service.internal;

import travel.ways.travelwaysapi._core.exception.ServerException;

import javax.servlet.http.Cookie;

public interface JwtService {
    String TOKEN_TYPE = "Bearer";
    String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    String generateJwt(String username);

    String getUserFromRefreshToken(String refreshToken);

    String refreshToken(String refreshToken) throws ServerException;

    String generateRefreshToken(String username);

    void revokeRefreshToken(String refreshToken);

    void authenticateUser(String jwt);

    Cookie getRefreshCookie(String refreshToken);

    Cookie getLogoutCookie();
}
