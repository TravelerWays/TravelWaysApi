package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class EditAttractionRequest {
    @NotNull
    private String attractionHash;
    @NotNull
    private String title;
    private String description;
    @NotNull
    private boolean isPublic;
    @NotNull
    private boolean isVisited;
    private Date visitedAt;
    private String tripHash;
    private Short rate;

    public boolean isValid() {
        if (isVisited) {
            return visitedAt == null && rate == null;
        }

        return true;
    }
}
