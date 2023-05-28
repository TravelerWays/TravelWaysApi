package travel.ways.travelwaysapi.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.UserFriends;
import travel.ways.travelwaysapi.user.model.dto.NotificationModel;
import travel.ways.travelwaysapi.user.model.dto.request.ChaneInvitationStatusRequest;
import travel.ways.travelwaysapi.user.model.enums.FriendsStatus;
import travel.ways.travelwaysapi.user.model.enums.NotificationType;
import travel.ways.travelwaysapi.user.repository.UserFriendRepository;
import travel.ways.travelwaysapi.user.service.shared.NotificationService;
import travel.ways.travelwaysapi.user.service.shared.UserFriendsService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFriendsServiceImpl implements UserFriendsService {
    private final UserService userService;
    private final UserFriendRepository userFriendRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    @SneakyThrows
    public void createInvitation(String userHash) {
        var loggedUser = userService.getLoggedUser();
        if (loggedUser.getHash().equals(userHash)) {
            throw new ServerException("You cannot send an invitation to yourself\n", HttpStatus.BAD_REQUEST);
        }
        var invitedFriend = userService.getByHash(userHash);

        if (userFriendRepository.existsByUserAndFriend(loggedUser, invitedFriend)) {
            throw new ServerException("This user is already your friend", HttpStatus.BAD_REQUEST);
        }

        if (userFriendRepository.hasPendingInvitation(loggedUser, invitedFriend)) {
            throw new ServerException("Invitation already exists in pending status", HttpStatus.BAD_REQUEST);

        }

        var invitationHash = UUID.randomUUID().toString();
        userFriendRepository.save(new UserFriends(
                invitationHash,
                loggedUser,
                invitedFriend,
                FriendsStatus.Pending
        ));
        notificationService.sendNotification(new NotificationModel(
                invitedFriend.getHash(),
                "New friend request from " + loggedUser.getName() + " " + loggedUser.getSurname(),
                invitationHash,
                false,
                NotificationType.NewInvitation
        ));
    }

    @Override
    @SneakyThrows
    @Transactional
    public void changeInvitationStatus(ChaneInvitationStatusRequest request) {
        var invitation = userFriendRepository.findByHash(request.getInvitationHash());

        var loggedUser = userService.getLoggedUser();
        if (invitation == null) {
            throw new ServerException("Invitation not found", HttpStatus.BAD_REQUEST);
        }

        if (invitation.getStatus() == FriendsStatus.Accepted) {
            throw new ServerException("Cannot update accepted invitation", HttpStatus.BAD_REQUEST);
        }

        if (!invitation.canEdit(loggedUser)) {
            throw new ServerException("User cannot edit it", HttpStatus.FORBIDDEN);
        }

        if (request.getStatus() == FriendsStatus.Accepted) {
            if (!invitation.getFriend().equals(loggedUser)) {
                throw new ServerException("User cannot accept own invitation", HttpStatus.FORBIDDEN);
            }

            userFriendRepository.save(new UserFriends(
                    UUID.randomUUID().toString(),
                    invitation.getFriend(),
                    invitation.getUser(),
                    FriendsStatus.Accepted
            ));
        }

        invitation.setStatus(request.getStatus());

    }

    @Override
    public List<UserFriends> getUserInvitation(String userHash) {
        return userFriendRepository.findUserInvitation(userHash, FriendsStatus.Pending);
    }

    @Override
    public List<AppUser> getUserFriends(AppUser user) {
        return userFriendRepository.findUserFriends(user);
    }

    @Override
    public boolean isLoggedUserUserFriend(AppUser user) {
        var loggedUser = userService.getLoggedUser();
        return userFriendRepository.existsByUserAndFriend(loggedUser, user);
    }
}
