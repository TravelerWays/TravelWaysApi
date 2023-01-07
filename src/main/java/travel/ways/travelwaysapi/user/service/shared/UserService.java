package travel.ways.travelwaysapi.user.service.shared;

import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface UserService {
    AppUser getByUsername(String username);

    List<AppUser> getAll();

    AppUser getByHash(String hash);

    void save(AppUser appUser);

    AppUser getLoggedUser();

    AppUser getTripOwner(Trip trip);
}