package travel.ways.travelwaysapi.trip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripInvitationRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripInvitationResponse;
import travel.ways.travelwaysapi.trip.service.internal.TripInvitationService;

import java.util.List;

@RestController
@RequestMapping("/api/trip/invitation")
@RequiredArgsConstructor
public class TripInvitationController {
    private final TripInvitationService tripInvitationService;

    @PostMapping
    public TripInvitationResponse createTripInvitation(@ModelAttribute CreateTripInvitationRequest createTripInvitationRequest) {
        return tripInvitationService.createTripInvitation(createTripInvitationRequest);
    }

    @DeleteMapping("/{invitationHash}")
    public BaseResponse deleteTripInvitation(@PathVariable String invitationHash) {
        tripInvitationService.deleteTripInvitation(invitationHash);
        return new BaseResponse(true, "invitation deleted.");
    }

    @PutMapping("/{invitationHash}/accept")
    public BaseResponse acceptInvitation(@PathVariable String invitationHash) {
        tripInvitationService.accept(invitationHash);
        return new BaseResponse(true, "user added to trip");
    }

    @PutMapping("/{invitationHash}/decline")
    public BaseResponse declineInvitation(@PathVariable String invitationHash) {
        tripInvitationService.decline(invitationHash);
        return new BaseResponse(true, "declined invitation");
    }

    @GetMapping("/all")
    public List<TripInvitationResponse> getAllInvitationForUser() {
        return tripInvitationService.getAll();
    }
}
