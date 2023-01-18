package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
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
