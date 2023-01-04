package travel.ways.travelwaysapi.trip.service.shared;

import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageToTripRequest;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripDto;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface TripService {
    Trip createTrip(CreateTripRequest createTripRequest);

    void deleteTrip(Trip trip);

    Trip getByHash(String hash);

    List<TripDto> getAllTripsForUser(AppUser user);

    Image getMainImage(String hash);

    Image getMainImage(Trip trip);

    void deleteMainImage(Trip trip);

    Trip editTrip(EditTripRequest request);

    Image editMainImage(Trip trip, MultipartFile data);

    Image addImage(AddImageToTripRequest request);

    boolean checkIfContributor(Trip trip, AppUser appUser);

    String getMainImageHash(Trip trip);

    void closeTrip(String hash);

    void openTrip(String hash);
}
