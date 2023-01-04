package travel.ways.travelwaysapi.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.db.ImageHashOnly;
import travel.ways.travelwaysapi.trip.model.db.Trip;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Image findImageByTrip_TripAndTrip_isMainTrue(Trip trip);

    ImageHashOnly findImageHashByTrip_TripAndTrip_isMainTrue(Trip trip);

    void deleteImageByHash(String hash);
}
