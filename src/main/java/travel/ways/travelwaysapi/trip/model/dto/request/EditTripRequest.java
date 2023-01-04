package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class EditTripRequest {
    @NotNull
    private String hash;
    @NotNull
    private String title;
    @NotNull
    private Boolean isPublic;
    private String description;
}
