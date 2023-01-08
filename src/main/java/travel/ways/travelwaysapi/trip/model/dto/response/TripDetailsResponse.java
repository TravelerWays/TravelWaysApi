package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.Getter;
import lombok.Setter;
import travel.ways.travelwaysapi.file.model.projection.ImageWithoutData;
import travel.ways.travelwaysapi.trip.model.db.Trip;

import java.util.List;

@Getter
@Setter
public class TripDetailsResponse extends TripResponse {
    private List<AttractionResponse> attractions;

    public TripDetailsResponse(String title, String hash, boolean isPublic, String description,
                               List<ImageWithoutData> images, boolean isOpen, List<AttractionResponse> attractions) {
        super(title, hash, isPublic, description, images, isOpen);
        this.attractions = attractions;
    }

    public static TripDetailsResponse of(Trip trip, List<ImageWithoutData> imagesWithoutData, List<AttractionResponse> attractions) {
        return new TripDetailsResponse(
                trip.getTitle(),
                trip.getHash(),
                trip.isPublic(),
                trip.getDescription(),
                imagesWithoutData,
                trip.isOpen(),
                attractions
        );
    }
}
