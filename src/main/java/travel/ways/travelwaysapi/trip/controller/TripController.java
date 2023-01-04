package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripDto;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/trip")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TripDto createTrip(@ModelAttribute CreateTripRequest createTripRequest, Authentication authentication) {
        AppUser user = userService.getByUsername(authentication.getName());
        Trip trip = tripService.createTrip(createTripRequest, user);
        return mapper.map(trip, TripDto.class);
    }

    @SneakyThrows
    @DeleteMapping
    public BaseResponse deleteTrip(@RequestParam String hash, Authentication authentication) {
        AppUser appUser = userService.getByUsername(authentication.getName());
        Trip trip = tripService.getByHash(hash);
        if (!appUser.equals(trip.findOwner()))
            throw new ServerException("You don't have permission to delete the trip", HttpStatus.UNAUTHORIZED);
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
    @PutMapping("/edit/{hash}")
    public TripDto editTrip(@PathVariable String hash, @RequestParam(required = false) String title,
                            @RequestParam(required = false) Boolean isPublic, Authentication authentication) {
        AppUser appUser = userService.getByUsername(authentication.getName());
        Trip trip = tripService.getByHash(hash);
        if (!appUser.equals(trip.findOwner())) {
            throw new ServerException("You don't have permission to edit the trip", HttpStatus.UNAUTHORIZED);
        }
        if (title != null) {
            trip = tripService.editTitle(trip, title);
        }
        if (isPublic != null) {
            trip = tripService.editIsPublic(trip, isPublic);
        }
        return mapper.map(trip, TripDto.class);
    }
}
