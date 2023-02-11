package travel.ways.travelwaysapi.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageRequest;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.user.model.dto.response.UserResponse;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ImageService imageService;

    @GetMapping("/all")
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/logged")
    public UserResponse getLogged() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userService.getByUsername(auth.getName());
        return UserResponse.of(user, imageService.getImageSummary(user));
    }

    @PostMapping(value = "/{userHash}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageSummaryDto addUserImage(@PathVariable String userHash, @Valid @ModelAttribute AddImageRequest addImageRequest) {
        Image image = userService.addImage(addImageRequest, userHash);
        return imageService.getImageSummary(image.getHash());
    }

    @DeleteMapping("/{userHash}/image")
    public BaseResponse deleteUserImage(@PathVariable String userHash) {
        userService.deleteImage(userHash);
        return new BaseResponse(true, "user image deleted");
    }
}
