package travel.way.travelwayapi.auth.services.internal;

import travel.way.travelwayapi._core.exceptions.ServerException;

public interface JwtUtils {
    public static final  String TOKEN_TYPE = "Bearer";
    String generateJwt(String username);
    String getUserFromRefreshToken(String refreshToken);
    String refreshToken(String refreshToken) throws ServerException;

    String generateRefreshToken(String username);
    void authenticationUser(String jwt);
}
