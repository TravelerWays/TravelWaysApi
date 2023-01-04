package travel.ways.travelwaysapi.file.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.dto.AddImageToTripRequest;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.service.shared.TripService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final TripService tripService;

    @GetMapping(value = "/trip")
    public ResponseEntity<byte[]> getTripMainImage(@RequestParam String hash) {
        Image image = tripService.getMainImage(hash);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", image.getExtension())
                .body(image.getData());
    }

    @DeleteMapping(value = "/trip/{hash}")
    public BaseResponse deleteTripMainImage(@PathVariable String hash) {
        Trip trip = tripService.getByHash(hash);
        tripService.deleteMainImage(trip);
        return new BaseResponse(true, "deleted main image");
    }

    @PutMapping(value = "/trip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> addImageToTrip(@Valid @ModelAttribute AddImageToTripRequest addImageToTripRequest) {
        Image image = tripService.addImage(addImageToTripRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Content-Type", image.getExtension())
                .body(image.getData());
    }
}
