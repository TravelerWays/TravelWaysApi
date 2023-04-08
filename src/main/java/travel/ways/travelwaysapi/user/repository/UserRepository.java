package travel.ways.travelwaysapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.user.model.db.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);

    AppUser findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    AppUser findByHash(String hash);

    AppUser findOwnerByTripsTripAndTripsIsOwnerTrue(Trip trip);

}
