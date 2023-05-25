package travel.ways.travelwaysapi.user.service.shared;

import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.user.model.dto.response.UserResponse;

import java.util.List;
import java.util.Set;

public interface UserService {
    AppUser getByUsername(String username);

    List<UserResponse> getAll();

    AppUser getByHash(String hash);

    void save(AppUser appUser);

    AppUser getLoggedUser();

    AppUser getTripOwner(Trip trip);

    Image addImage(AddImageRequest request, AppUser user);

    void deleteImage(AppUser user);

    Set<AppUser> search(String query);
}