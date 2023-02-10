package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.trip.model.db.Trip;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripResponse {
    private String title;
    private String hash;
    private boolean isPublic;
    private String description;
    private List<ImageSummaryDto> images;
    private boolean isOpen;

    public static TripResponse of(Trip trip, List<ImageSummaryDto> imagesWithoutData) {
        return new TripResponse(
                trip.getTitle(),
                trip.getHash(),
                trip.isPublic(),
                trip.getDescription(),
                imagesWithoutData,
                trip.isOpen()
        );
    }
}
