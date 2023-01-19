package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import travel.ways.travelwaysapi.trip.model.db.TripInvitation;

@Getter
@Setter
@AllArgsConstructor
public class TripInvitationResponse {
    private String tripHash;
    private String userHash;
    private Boolean active;
    private Boolean accepted;
    private String invitationHash;

    public static TripInvitationResponse of(TripInvitation tripInvitation) {
        return new TripInvitationResponse(
                tripInvitation.getTrip().getHash(),
                tripInvitation.getUser().getHash(),
                tripInvitation.getActive(),
                tripInvitation.getAccepted(),
                tripInvitation.getHash()
        );
    }
}
