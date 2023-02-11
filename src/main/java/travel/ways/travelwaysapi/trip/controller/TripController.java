package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.dto.AddImageRequest;
import travel.ways.travelwaysapi.file.model.dto.ImageSummaryDto;
import travel.ways.travelwaysapi.file.service.shared.ImageService;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripMainImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripDetailsResponse;
import travel.ways.travelwaysapi.trip.model.dto.response.TripResponse;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/trip")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final UserService userService;
    private final AttractionService attractionService;
    private final ImageService imageService;


    @PostMapping
    public TripResponse createTrip(@Valid @RequestBody CreateTripRequest createTripRequest) {
        var trip = tripService.createTrip(createTripRequest);
        return TripResponse.of(trip, new ArrayList<>());
    }

    @DeleteMapping("/{tripHash}")
    public BaseResponse deleteTrip(@PathVariable String tripHash) {
        tripService.deleteTrip(tripService.getTrip(tripHash));
        return new BaseResponse(true, "trip deleted");
    }

    @GetMapping("/all/{userHash}")
    public List<TripResponse> getUserTrips(@PathVariable String userHash) {
        return tripService.getUserTrips(userService.getByHash(userHash));
    }

    @GetMapping("/all")
    public List<TripResponse> getLoggedUserTrips() {
        return tripService.getUserTrips(userService.getLoggedUser());
    }

    @GetMapping("/{tripHash}")
    public TripDetailsResponse getTrip(@PathVariable String tripHash) {
        var trip = tripService.getTrip(tripHash);
        return TripDetailsResponse.of(trip, tripService.getImageSummaryList(trip),
                attractionService.getTripAttractions(trip));
    }

    @PutMapping("/edit")
    public TripResponse editTrip(@Valid @RequestBody EditTripRequest editTripRequest) {
        var trip = tripService.editTrip(editTripRequest);
        return TripResponse.of(trip, tripService.getImageSummaryList(trip));
    }

    @PutMapping("/edit/main-image")
    public BaseResponse editMainImage(@Valid @RequestBody EditTripMainImageRequest editMainImageRequest) {

        var trip = tripService.getTrip(editMainImageRequest.getTripHash());
        var image = tripService.editMainImage(trip, editMainImageRequest.getImageHash());
        if (image == null) {
            return new BaseResponse(true, "main image removed");
        }
        return new BaseResponse(true, "main image changed");
    }

    @PutMapping("/close/{tripHash}")
    public BaseResponse closeTrip(@PathVariable String tripHash) {
        tripService.closeTrip(tripHash);
        return new BaseResponse(true, "trip closed");
    }

    @PutMapping("/open/{tripHash}")
    public BaseResponse openTrip(@PathVariable String tripHash) {
        tripService.openTrip(tripHash);
        return new BaseResponse(true, "trip opened");
    }

    @PostMapping(value = "/{tripHash}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImageSummaryDto addImageToTrip(@PathVariable String tripHash, @Valid @ModelAttribute AddImageRequest addImageRequest) {
        var image = tripService.addImage(addImageRequest, tripHash);
        return imageService.getImageSummary(image.getHash());
    }

    @DeleteMapping("/image/{imageHash}")
    public BaseResponse deleteImage(@PathVariable String imageHash) {
        tripService.deleteImage(imageHash);
        return new BaseResponse(true, "image deleted");
    }

    @DeleteMapping("/{tripHash}/user/{userHash}")
    public BaseResponse deleteUserFromTrip(@PathVariable String tripHash, @PathVariable String userHash) {
        tripService.deleteUserFromTrip(userHash, tripHash);
        return new BaseResponse(true, "user deleted from trip");
    }
}
