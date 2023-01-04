package travel.ways.travelwaysapi.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final TripService tripService;
    private final UserService userService;
    private final ImageService imageService;

    @GetMapping(value = "/trip")
    public ResponseEntity<byte[]> getTripMainImage(@RequestParam String hash) {
        Image image = tripService.getMainImage(hash);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", image.getExtension())
                .body(image.getData());
    }

    @SneakyThrows
    @DeleteMapping(value = "/trip")
    public BaseResponse deleteTripMainImage(@RequestParam String hash, Authentication authentication) {
        AppUser user = userService.getByUsername(authentication.getName());
        Trip trip = tripService.getByHash(hash);
        if (!user.equals(trip.findOwner())) {
            throw new ServerException("You don't have permission to delete the image", HttpStatus.UNAUTHORIZED);
        }
        tripService.deleteMainImage(trip);
        return new BaseResponse(true, "deleted main image");
    }

    @PutMapping("/trip")
    @SneakyThrows
    public ResponseEntity<byte[]> addImageToTrip(@RequestParam MultipartFile data, @RequestParam String hash,
                                                 @RequestParam(required = false) boolean isMain, Authentication authentication) {
        Trip trip = tripService.getByHash(hash);
        AppUser appUser = userService.getByUsername(authentication.getName());
        Image image = imageService.createImage(trip.getTitle(), data.getBytes());
        if (isMain) {
            if (!appUser.equals(trip.findOwner())) {
                throw new ServerException("You don't have permission to edit the image", HttpStatus.UNAUTHORIZED);
            }
            tripService.editMainImage(trip, image);
        } else if (tripService.checkIfContributor(trip, appUser)) {
            tripService.addImage(trip, image);
        } else {
            throw new ServerException("You don't have permission to add image", HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", image.getExtension())
                .body(image.getData());
    }
}
