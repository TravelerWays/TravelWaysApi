package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;

import java.util.Date;

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

    public static AttractionResponse of(Attraction attraction){
        return new AttractionResponse(
                attraction.getHash(),
                attraction.getTitle(),
                attraction.getDescription(),
                attraction.isPublic(),
                attraction.isVisited(),
                attraction.getVisitedAt(),
                attraction.getRate()
        );
    }
}
