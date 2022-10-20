package travel.way.travelwayapi.user.shared;

import org.springframework.security.core.userdetails.User;
import travel.way.travelwayapi.user.models.db.AppUser;
import travel.way.travelwayapi.user.models.dto.request.CreateUserRequest;
import travel.way.travelwayapi.user.models.dto.response.UserDto;

import java.util.List;

public interface UserService {
    AppUser createUser(CreateUserRequest request);
    AppUser getByUsername(String username);
    List<AppUser> getAll();
}
