package travel.ways.travelwaysapi.trip.service.shared;

import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.ImageDto;
import travel.ways.travelwaysapi.trip.model.dto.response.TripResponse;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface TripService {
    Trip createTrip(CreateTripRequest createTripRequest);

    void deleteTrip(Trip trip);

    Trip getTrip(String hash);

    List<TripResponse> getUserTrips(AppUser user);

    Trip editTrip(EditTripRequest request);

    ImageDto editMainImage(Trip trip, String newImageHash);

    ImageDto addImage(AddImageRequest request, String tripHash);

    boolean checkIfContributor(Trip trip, AppUser appUser);

    void closeTrip(String hash);

    void openTrip(String hash);

    AppUser findOwner(Trip trip);

    void deleteImage(String hash);

    List<ImageDto> getImageSummaryList(Trip trip);

    Trip getTripByImageHash(String imageHash);

    void deleteUserFromTrip(String userHash, String tripHash);
}
