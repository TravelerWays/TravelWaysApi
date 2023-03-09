package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAttractionRequest {
    private String osmId;
    private String title;
    private String description;
    private boolean isPublic;
    private boolean isVisited;
    private Date visitedAt;
    private String tripHash;
    private Short rate;
}
