package travel.ways.travelwaysapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.user.model.dto.NotificationModel;
import travel.ways.travelwaysapi.user.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.user.model.dto.request.ChaneInvitationStatusRequest;
import travel.ways.travelwaysapi.user.model.dto.request.UpdatePasswordRequest;
import travel.ways.travelwaysapi.user.model.dto.request.UpdateUserRequest;
import travel.ways.travelwaysapi.user.model.dto.response.UserResponse;
import travel.ways.travelwaysapi.user.service.shared.AccountService;
import travel.ways.travelwaysapi.user.service.shared.NotificationService;
import travel.ways.travelwaysapi.user.service.shared.UserFriendsService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ImageService imageService;
    private final UserFriendsService userFriendsService;
    private final NotificationService notificationService;
    private final AccountService accountService;

    @GetMapping("/logged")
    public UserResponse getLogged() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.getByUsername(auth.getName());
        return UserResponse.of(user, imageService.getImageSummary(user));
    }

    @GetMapping("/friends")
    public List<UserResponse> getUserFriends() {
        var user = userService.getLoggedUser();
        return userFriendsService.getUserFriends(user).stream().map(x -> UserResponse.of(x, imageService.getImageSummary(x))).toList();
    }

    @PutMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageSummaryDto addUserImage( @Valid @ModelAttribute AddImageRequest addImageRequest) {
        var loggedUser = userService.getLoggedUser();
        var image = userService.addImage(addImageRequest, loggedUser);
        return imageService.getImageSummary(image.getHash());
    }

    @DeleteMapping("/image")
    public BaseResponse deleteUserImage() {
        var loggedUser = userService.getLoggedUser();
        userService.deleteImage(loggedUser);
        return new BaseResponse(true, "user image deleted");
    }

    @GetMapping("/search")
    public List<UserResponse> Search(@RequestParam("query") String query) {
        return userService.search(query)
                .stream().map(x -> UserResponse.of(x, null)).toList();
    }

    @GetMapping("/notification")
    public List<NotificationModel> GetUserNotification() {
        var appUser = userService.getLoggedUser();
        return notificationService.getUserNotification(appUser).stream().map(NotificationModel::of).toList();
    }


    @PutMapping("/notification")
    public ResponseEntity<Void> MarkAllNotificationAsRead() {
        var loggedUser = userService.getLoggedUser();
        notificationService.markAllUserNotificationAsRead(loggedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invite")
    public BaseResponse invite(@RequestBody String userHash) {
        userFriendsService.createInvitation(userHash);
        return BaseResponse.success();
    }

    @PutMapping("/invite")
    public BaseResponse changeInvitationStatus(@RequestBody ChaneInvitationStatusRequest request) {
        userFriendsService.changeInvitationStatus(request);
        notificationService.removeNotificationForObject(request.getInvitationHash());
        return BaseResponse.success();
    }

    @PutMapping("user-data")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UpdateUserRequest request){
        var loggedUser = userService.getLoggedUser();
        accountService.updateUser(request);
        var user = userService.getByHash(loggedUser.getHash());
        return ResponseEntity.ok(UserResponse.of(user, imageService.getImageSummary(user)));
    }

    @PutMapping("password")
    public ResponseEntity<BaseResponse> changePassword(@RequestBody UpdatePasswordRequest request){
        accountService.chanePassword(request);
        return ResponseEntity.ok(BaseResponse.success());
    }


}
