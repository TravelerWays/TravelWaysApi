package travel.ways.travelwaysapi.user.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi.user.model.enums.FriendsStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChaneInvitationStatusRequest {
    private FriendsStatus status;
    private String invitationHash;
}
