package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripMainImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripDetailsResponse;
import travel.ways.travelwaysapi.trip.model.dto.response.TripResponse;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/trip")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final UserService userService;
    private final AttractionService attractionService;


    @PostMapping
    public TripResponse createTrip(@Valid @RequestBody CreateTripRequest createTripRequest) {
        Trip trip = tripService.createTrip(createTripRequest);
        return TripResponse.of(trip, tripService.getAllImagesWithoutData(trip));
    }

    @DeleteMapping("/{hash}")
    public BaseResponse deleteTrip(@PathVariable String hash) {
        tripService.deleteTrip(tripService.getTrip(hash));
        return new BaseResponse(true, "trip deleted");
    }

    @GetMapping("/all/{userHash}")
    public List<TripResponse> getUserTrips(@PathVariable String userHash) {
        return tripService.getUserTrips(userService.getByUsername(userHash));
    }

    @GetMapping("/all")
    public List<TripResponse> getLoggedUserTrips() {
        return tripService.getUserTrips(userService.getLoggedUser());
    }

    @GetMapping("/{hash}")
    public TripDetailsResponse getTrip(@PathVariable String hash) {
        Trip trip = tripService.getTrip(hash);
        return TripDetailsResponse.of(trip, tripService.getAllImagesWithoutData(trip),
                attractionService.getTripAttractions(trip));
    }

    @PutMapping("/edit")
    public TripResponse editTrip(@Valid @RequestBody EditTripRequest editTripRequest) {
        Trip trip = tripService.editTrip(editTripRequest);
        return TripResponse.of(trip, tripService.getAllImagesWithoutData(trip));
    }

    @PutMapping("/edit/main-image")
    public BaseResponse editMainImage(@Valid @RequestBody EditTripMainImageRequest editMainImageRequest) {

        Trip trip = tripService.getTrip(editMainImageRequest.getTripHash());
        Image image = tripService.editMainImage(trip, editMainImageRequest.getImageHash());
        if (image == null) {
            return new BaseResponse(true, "main image removed");
        }
        return new BaseResponse(true, "main image changed");
    }

    @PutMapping("close/{hash}")
    public BaseResponse closeTrip(@PathVariable String hash) {
        tripService.closeTrip(hash);
        return new BaseResponse(true, "trip closed");
    }

    @PutMapping("open/{hash}")
    public BaseResponse openTrip(@PathVariable String hash) {
        tripService.openTrip(hash);
        return new BaseResponse(true, "trip opened");
    }
}
