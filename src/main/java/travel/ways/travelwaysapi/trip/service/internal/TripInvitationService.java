package travel.ways.travelwaysapi.trip.service.internal;

import travel.ways.travelwaysapi.trip.model.db.TripInvitation;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripInvitationRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripInvitationResponse;

import java.util.List;

public interface TripInvitationService {
    TripInvitationResponse createTripInvitation(CreateTripInvitationRequest request);

    void deleteTripInvitation(String invitationHash);

    TripInvitation getByHash(String invitationHash);

    void accept(String invitationHash);

    void decline(String invitationHash);

    List<TripInvitationResponse> getAll();
}
