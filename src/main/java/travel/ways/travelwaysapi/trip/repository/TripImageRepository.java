package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.trip.model.db.TripImage;
import travel.ways.travelwaysapi.trip.model.db.TripImageId;

import java.util.List;

public interface TripImageRepository extends JpaRepository<TripImage, TripImageId> {

    TripImage findByImageHash(String imageHash);

    @Query("select t.image.id from TripImage t where t.id.tripId = ?1")
    List<Long> findAllImageIdInTrip(Long tripId);

    @Query("select (count(t) > 0) from TripImage t where t.id.imageId = ?1 and t.isMain = true")
    boolean isMain(Long imageId);

    @Query("select (count(t) > 0) from TripImage t where t.image.hash = ?1 and t.isMain = true")
    boolean isMain(String imageHash);

    @Transactional
    @Modifying
    @Query("update TripImage t set t.isMain = false where t.trip = ?1")
    void unsetMainImageForTrip(Trip trip);
    

    @Query("select (count(t) > 0) from TripImage t where t.id.tripId = ?1 and t.image.hash = ?2")
    boolean existsImageInTrip(Long tripId, String hash);
}
