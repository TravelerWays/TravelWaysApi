package travel.ways.travelwaysapi.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.ImageWithoutData;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageToTripRequest;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final TripService tripService;
    private final ImageService imageService;

    @DeleteMapping(value = "/{hash}")
    public BaseResponse deleteImage(@PathVariable String hash) {
        tripService.deleteImage(hash);
        return new BaseResponse(true, "deleted image");
    }

    @PutMapping(value = "/trip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageWithoutData addImageToTrip(@Valid @ModelAttribute AddImageToTripRequest addImageToTripRequest) {
        Image image = tripService.addImage(addImageToTripRequest);
        return imageService.getImageWithoutData(image.getHash());
    }

    @GetMapping("/{hash}")
    public ResponseEntity<byte[]> getImage(@PathVariable String hash) {
        Image image = imageService.getImage(hash);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", image.getExtension())
                .body(image.getData());
    }

}
