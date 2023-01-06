package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditMainImageRequest;
import travel.ways.travelwaysapi.trip.model.dto.request.EditTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripDto;
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


    @PostMapping
    public TripDto createTrip(@Valid @RequestBody CreateTripRequest createTripRequest) {
        Trip trip = tripService.createTrip(createTripRequest);
        return tripService.getTripDto(trip.getHash());
    }

    @DeleteMapping("/{hash}")
    public BaseResponse deleteTrip(@PathVariable String hash) {
        tripService.deleteTrip(tripService.getTrip(hash));
        return new BaseResponse(true, "trip deleted");
    }

    @GetMapping("/all/{username}")
    public List<TripDto> getAllTripsForUser(@PathVariable String username) {
        return tripService.getAllTripsForUser(userService.getByUsername(username));
    }

    @GetMapping("/{hash}")
    public TripDto getTrip(@PathVariable String hash) {
        Trip trip = tripService.getTrip(hash);
        return tripService.getTripDto(trip);
    }

    @PutMapping("/edit")
    public TripDto editTrip(@Valid @RequestBody EditTripRequest editTripRequest) {
        Trip trip = tripService.editTrip(editTripRequest);
        return tripService.getTripDto(trip.getHash());
    }

    @PutMapping("/edit/main-image")
    public BaseResponse editMainImage(@Valid @RequestBody EditMainImageRequest editMainImageRequest) {

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
