package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.trip.model.db.trip.TripInvitation;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface TripInvitationRepository extends JpaRepository<TripInvitation, Long> {
    TripInvitation findByHash(String hash);
    List<TripInvitation> findAllByUserAndActiveTrue(AppUser user);
}
