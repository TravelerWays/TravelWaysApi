package travel.ways.travelwaysapi.user.service.internal;

public interface AccountManager {
    void changePassword(Long userId, String newPassword);
}
