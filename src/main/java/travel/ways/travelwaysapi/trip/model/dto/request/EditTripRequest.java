package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditTripRequest {
    @NotNull
    private String hash;
    @NotNull
    private String title;
    @NotNull
    private Boolean isPublic;
    private String description;
}
