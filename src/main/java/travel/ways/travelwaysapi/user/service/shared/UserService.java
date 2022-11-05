package travel.ways.travelwaysapi.user.service.shared;

import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.request.CreateUserRequest;

import java.util.List;

public interface UserService {
    AppUser getByUsername(String username);
    List<AppUser> getAll();
}
