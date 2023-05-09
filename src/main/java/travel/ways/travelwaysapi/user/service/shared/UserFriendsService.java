package travel.ways.travelwaysapi.user.service.shared;

import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.UserFriends;
import travel.ways.travelwaysapi.user.model.dto.request.ChaneInvitationStatusRequest;

import java.util.List;

public interface UserFriendsService {
    void createInvitation(String userHash);

    void changeInvitationStatus(ChaneInvitationStatusRequest request);

    List<UserFriends> getUserInvitation(String userHash);

    List<AppUser> getUserFriends(AppUser user);

}
