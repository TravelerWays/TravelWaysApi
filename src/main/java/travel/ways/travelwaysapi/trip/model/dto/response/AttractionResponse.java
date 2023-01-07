package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.file.model.projection.ImageWithoutData;

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
    private List<ImageWithoutData> images;
    private String tripHash;
}
