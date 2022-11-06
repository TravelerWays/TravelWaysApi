package travel.ways.travelwaysapi.user.service.shared;

import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;

public interface AccountService {
    void changePassword(Long userId, String newPassword);

    void registerUser(CreateUserRequest user);

    void confirmUser(String hash);
}
