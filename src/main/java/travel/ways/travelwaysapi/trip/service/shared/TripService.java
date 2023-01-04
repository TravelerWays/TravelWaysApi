package travel.ways.travelwaysapi.trip.service.shared;

import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripDto;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface TripService {
    Trip createTrip(CreateTripRequest createTripRequest, AppUser user);

    void deleteTrip(Trip trip);

    Trip getByHash(String hash);

    List<TripDto> getAllTripsForUser(AppUser user);

    Image getMainImage(String hash);

    Image getMainImage(Trip trip);

    void deleteMainImage(Trip trip);

    Trip editTitle(Trip trip, String title);

    Trip editIsPublic(Trip trip, Boolean isPublic);

    void editMainImage(Trip trip, Image image);

    void addImage(Trip trip, Image image);

    boolean checkIfContributor(Trip trip, AppUser appUser);
}
