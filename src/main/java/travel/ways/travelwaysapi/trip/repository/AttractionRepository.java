package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import travel.ways.travelwaysapi.trip.model.db.attraction.Attraction;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    @Query("select a from Attraction a where a.user = ?1 order by a.visitedAt desc")
    List<Attraction> findAllByUser(AppUser user);

    Attraction findByHash(String attractionHash);

    Attraction findByImagesImageHash(String imageHash);

    List<Attraction> findAllByTrip(Trip trip);
}
