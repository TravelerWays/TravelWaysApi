package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddUserToTripRequest {
    @NotNull
    private String username;
    @NotNull
    private String tripHash;
}
