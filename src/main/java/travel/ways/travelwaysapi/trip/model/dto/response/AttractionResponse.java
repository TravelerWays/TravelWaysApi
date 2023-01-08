package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.file.model.projection.ImageWithoutData;
import travel.ways.travelwaysapi.map.model.dto.response.LocationResponse;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttractionResponse {
    private String hash;
    private String title;
    private String description;
    private boolean isPublic;
    private boolean isVisited;
    private Date visitedAt;
    private Short rate;
    private LocationResponse locationResponse;
    private List<ImageWithoutData> images;
    private String tripHash;

    public static AttractionResponse of(Attraction attraction, List<ImageWithoutData> imagesWithoutData) {
        Trip trip = attraction.getTrip();
        String tripHash = null;
        if (trip != null) {
            tripHash = trip.getHash();
        }

        return new AttractionResponse(
                attraction.getHash(),
                attraction.getTitle(),
                attraction.getDescription(),
                attraction.isPublic(),
                attraction.isVisited(),
                attraction.getVisitedAt(),
                attraction.getRate(),
                LocationResponse.of(attraction.getLocation()),
                imagesWithoutData,
                tripHash
        );
    }
}
