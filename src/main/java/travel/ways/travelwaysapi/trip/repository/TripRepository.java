package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.trip.model.db.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
    Trip findByHash(String hash);
}
