package travel.ways.travelwaysapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.dto.request.AddImageRequest;
import travel.ways.travelwaysapi.user.model.dto.NotificationModel;
import travel.ways.travelwaysapi.user.model.dto.request.ChaneInvitationStatusRequest;
import travel.ways.travelwaysapi.user.model.dto.response.UserResponse;
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

    @GetMapping("/logged")
    public UserResponse getLogged() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.getByUsername(auth.getName());
        return UserResponse.of(user, imageService.getImageSummary(user));
    }

    @PostMapping(value = "/{userHash}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageSummaryDto addUserImage(@PathVariable String userHash, @Valid @ModelAttribute AddImageRequest addImageRequest) {
        var image = userService.addImage(addImageRequest, userHash);
        return imageService.getImageSummary(image.getHash());
    }

    @DeleteMapping("/{userHash}/image")
    public BaseResponse deleteUserImage(@PathVariable String userHash) {
        userService.deleteImage(userHash);
        return new BaseResponse(true, "user image deleted");
    }

    @GetMapping("/search")
    public List<UserResponse> Search(@RequestParam String query) {
        return userService.search(query)
                .stream().map(x -> UserResponse.of(x, null)).toList();
    }

    @GetMapping("/notification")
    public List<NotificationModel> GetUserNotification() {
        var appUser = userService.getLoggedUser();
        return notificationService.getUserNotification(appUser).stream().map(NotificationModel::of).toList();
    }

    @PostMapping("/invite")
    public BaseResponse invite(@RequestBody String userHash) {
        userFriendsService.createInvitation(userHash);
        return BaseResponse.success();
    }

    @PutMapping("/invite")
    public BaseResponse changeInvitationStatus(@RequestBody ChaneInvitationStatusRequest request) {
        userFriendsService.changeInvitationStatus(request);
        return BaseResponse.success();
    }


}
