package travel.ways.travelwaysapi.trip.service.shared;

import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageRequest;
import travel.ways.travelwaysapi.file.model.projection.ImageWithoutData;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripDetailsResponse;
import travel.ways.travelwaysapi.trip.model.dto.response.TripResponse;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface TripService {
    Trip createTrip(CreateTripRequest createTripRequest);

    void deleteTrip(Trip trip);

    Trip getTrip(String hash);

    List<TripResponse> getUserTrips(AppUser user);

    Trip editTrip(EditTripRequest request);

    Image editMainImage(Trip trip, String newImageHash);

    Image addImage(AddImageRequest request);

    boolean checkIfContributor(Trip trip, AppUser appUser);

    void closeTrip(String hash);

    void openTrip(String hash);

    AppUser findOwner(Trip trip);

    void deleteImage(String hash);

    List<ImageWithoutData> getAllImagesWithoutData(Trip trip);

    TripResponse createTripResponse(String hash);

    TripResponse createTripResponse(Trip trip);

    TripDetailsResponse createTripDetailsResponse(Trip sourceTrip);
}
