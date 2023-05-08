package travel.ways.travelwaysapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);

    AppUser findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    AppUser findByHash(String hash);

    AppUser findOwnerByTripsTripAndTripsIsOwnerTrue(Trip trip);

    @Query("""
            select a from AppUser a
            where  lower( a.name) like lower( concat(?1, '%')) or lower( a.surname) like lower( concat(?1, '%')) and a.active = true""")
    List<AppUser> searchAppUserByNameOrSurname(String query);

}
