package travel.ways.travelwaysapi.user.service.shared;

import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.request.ChangePasswordRequest;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;
import travel.ways.travelwaysapi.user.model.dto.request.UpdatePasswordRequest;
import travel.ways.travelwaysapi.user.model.dto.request.UpdateUserRequest;

public interface AccountService {
    void changePassword(Long userId, String newPassword);

    void chanePassword(UpdatePasswordRequest request);
    void updateUser(UpdateUserRequest request);

    AppUser createUser(CreateUserRequest user);

    AppUser activateUser(String hash);

    void sendActivationMail(AppUser user);
}