package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.db.TripInvitation;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateTripInvitationRequest;
import travel.ways.travelwaysapi.trip.model.dto.response.TripInvitationResponse;
import travel.ways.travelwaysapi.trip.repository.TripInvitationRepository;
import travel.ways.travelwaysapi.trip.service.internal.TripInvitationService;
import travel.ways.travelwaysapi.trip.service.shared.TripService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TripInvitationServiceImpl implements TripInvitationService {
    private final TripInvitationRepository tripInvitationRepository;
    private final UserService userService;
    private final TripService tripService;

    @Override
    @Transactional
    @SneakyThrows
    public TripInvitationResponse createTripInvitation(CreateTripInvitationRequest request) {
        Trip trip = tripService.getTrip(request.getTripHash());
        if (!tripService.checkIfContributor(trip, userService.getLoggedUser())) {
            throw new ServerException("you do not have permission to invite user to the trip", HttpStatus.FORBIDDEN);
        }
        TripInvitation tripInvitation = new TripInvitation(trip, userService.getByHash(request.getUserHash()));
        tripInvitationRepository.save(tripInvitation);
        return TripInvitationResponse.of(tripInvitation);
    }

    @Override
    @Transactional
    public void deleteTripInvitation(String invitationHash) {
        TripInvitation tripInvitation = this.getByHash(invitationHash);
        tripInvitationRepository.delete(tripInvitation);
    }

    @Override
    @SneakyThrows
    public TripInvitation getByHash(String invitationHash) {
        TripInvitation tripInvitation = tripInvitationRepository.findByHash(invitationHash);
        if (tripInvitation == null) {
            throw new ServerException("Can not find invitation", HttpStatus.NOT_FOUND);
        }
        AppUser loggedUser = userService.getLoggedUser();
        if (!loggedUser.equals(userService.getTripOwner(tripInvitation.getTrip()))
                && !loggedUser.equals(tripInvitation.getUser())) {
            throw new ServerException("You do not have permission to see invitation", HttpStatus.FORBIDDEN);
        }
        return tripInvitation;
    }

    @Override
    @SneakyThrows
    @Transactional
    public void accept(String invitationHash) {
        TripInvitation tripInvitation = this.getByHash(invitationHash);
        AppUser invitedUser = tripInvitation.getUser();
        if (!userService.getLoggedUser().equals(invitedUser)) {
            throw new ServerException("You do not have permission to accept invitation", HttpStatus.FORBIDDEN);
        }
        Trip trip = tripInvitation.getTrip();
        invitedUser.addTrip(trip);
        tripInvitation.setAccepted(true);
        tripInvitation.setActive(false);
    }

    @Override
    @SneakyThrows
    @Transactional
    public void decline(String invitationHash) {
        TripInvitation tripInvitation = this.getByHash(invitationHash);
        AppUser invitedUser = tripInvitation.getUser();
        if (!userService.getLoggedUser().equals(invitedUser)) {
            throw new ServerException("You do not have permission to decline invitation", HttpStatus.FORBIDDEN);
        }
        if (!tripInvitation.getActive()) {
            throw new ServerException("invitation is no longer active", HttpStatus.BAD_REQUEST);
        }
        tripInvitation.setAccepted(false);
        tripInvitation.setActive(false);
    }

    @Override
    public List<TripInvitationResponse> getAll() {
        List<TripInvitationResponse> invitations = new ArrayList<>();
        for (TripInvitation tripInvitation : tripInvitationRepository.findAllByUserAndActiveTrue(userService.getLoggedUser())) {
            invitations.add(TripInvitationResponse.of(tripInvitation));
        }
        return invitations;
    }
}
