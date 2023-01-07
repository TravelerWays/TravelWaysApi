package travel.ways.travelwaysapi.trip.service.internal;

import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageRequest;
import travel.ways.travelwaysapi.file.model.projection.ImageWithoutData;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditAttractionRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.AttractionResponse;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface AttractionService {
    Attraction createAttraction(CreateAttractionRequest createAttractionRequest);

    Image addImage(AddImageRequest request);

    Image editMainImage(Attraction attraction, String newMainImageHash);

    List<AttractionResponse> getUserAttractions(AppUser user);

    @Transactional
    @SneakyThrows
    Attraction addAttractionToTrip(Attraction attraction, Trip trip);

    List<AttractionResponse> getTripAttractions(Trip trip);

    void deleteAttraction(String attractionHash);

    Attraction getAttraction(String attractionHash);

    boolean checkIfContributor(Attraction attraction, AppUser appUser);

    @SneakyThrows
    List<ImageWithoutData> getAllImagesWithoutData(Attraction attraction);

    AttractionResponse createAttractionResponse(Attraction attraction);

    void deleteImage(String imageHash);

    Attraction editAttraction(EditAttractionRequest request);
}
