package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
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
    private final ModelMapper mapper = new ModelMapper();


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TripDto createTrip(@Valid @ModelAttribute CreateTripRequest createTripRequest) {
        Trip trip = tripService.createTrip(createTripRequest);
        return mapper.map(trip, TripDto.class);
    }

    @SneakyThrows
    @DeleteMapping("/{hash}")
    public BaseResponse deleteTrip(@PathVariable String hash) {
        Trip trip = tripService.getByHash(hash);
        tripService.deleteTrip(trip);
        return new BaseResponse(true, "trip deleted");
    }

    @GetMapping("/all")
    public List<TripDto> getAllTripsForUser(@RequestParam String username) {
        return tripService.getAllTripsForUser(userService.getByUsername(username));
    }

    @GetMapping
    public TripDto getTrip(@RequestParam String hash) {
        return mapper.map(tripService.getByHash(hash), TripDto.class);
    }

    @SneakyThrows
    @PutMapping(value = "/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TripDto editTrip(@Valid @ModelAttribute EditTripRequest editTripRequest) {
        Trip trip = tripService.editTrip(editTripRequest);
        return mapper.map(trip, TripDto.class);
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
