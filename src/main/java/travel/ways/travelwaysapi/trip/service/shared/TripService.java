package travel.ways.travelwaysapi.trip.service.shared;

import travel.ways.travelwaysapi.file.model.ImageWithoutData;
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

    Trip getTrip(String hash);

    List<TripDto> getAllTripsForUser(AppUser user);

    Trip editTrip(EditTripRequest request);

    Image editMainImage(Trip trip, String newImageHash);

    Image addImage(AddImageToTripRequest request);

    boolean checkIfContributor(Trip trip, AppUser appUser);

    void closeTrip(String hash);

    void openTrip(String hash);

    AppUser findOwner(Trip trip);

    void deleteImage(String hash);

    List<ImageWithoutData> getAllImagesWithoutData(Trip trip);

    TripDto getTripDto(String hash);

    TripDto getTripDto(Trip trip);
}
