package travel.ways.travelwaysapi.trip.service.internal;

import travel.ways.travelwaysapi.trip.model.db.attraction.Attraction;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.AttractionResponse;
import travel.ways.travelwaysapi.trip.model.dto.response.ImageDto;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface AttractionService {
    Attraction createAttraction(CreateAttractionRequest createAttractionRequest);

    ImageDto addImage(AddImageRequest request, String attractionHash);

    void editMainImage(Attraction attraction, String newMainImageHash);

    List<AttractionResponse> getUserAttractions(AppUser user);

    Attraction addAttractionToTrip(Attraction attraction, Trip trip);

    List<AttractionResponse> getTripAttractions(Trip trip);

    void deleteAttraction(String attractionHash);

    Attraction getAttraction(String attractionHash);

    boolean checkIfContributor(Attraction attraction, AppUser appUser);

    List<ImageDto> getImageSummaryList(Attraction attraction);

    void deleteImage(String imageHash);

    Attraction editAttraction(EditAttractionRequest request);

    Attraction getAttractionByImageHash(String imageHash);
}
