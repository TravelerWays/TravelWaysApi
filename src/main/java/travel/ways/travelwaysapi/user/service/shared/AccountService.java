package travel.ways.travelwaysapi.user.service.shared;

import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;

public interface AccountService {
    void changePassword(Long userId, String newPassword);

    AppUser createUser(CreateUserRequest user);

    void activateUser(String hash);

    void sendActivationMail(AppUser user);
}
