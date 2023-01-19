package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTripInvitationRequest {
    private String tripHash;
    private String userHash;
}
