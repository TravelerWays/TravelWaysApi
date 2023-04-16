package travel.ways.travelwaysapi.trip.service.internal;

import travel.ways.travelwaysapi.trip.model.db.trip.TripInvitation;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripInvitationRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripInvitationResponse;

import java.util.List;

public interface TripInvitationService {
    TripInvitationResponse createTripInvitation(CreateTripInvitationRequest request);

    void deleteTripInvitation(String invitationHash);

    TripInvitation getByHash(String invitationHash);

    void updateInvitation(String invitationHash, Boolean accepted);

    List<TripInvitationResponse> getAllActiveForLoggedUser();
}
