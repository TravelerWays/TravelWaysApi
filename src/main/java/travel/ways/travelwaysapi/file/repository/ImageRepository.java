package travel.ways.travelwaysapi.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.projection.ImageWithoutData;
import travel.ways.travelwaysapi.trip.model.db.Trip;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Long> {

    ImageWithoutData findImageWithoutDataByTripTripAndTripIsMainTrue(Trip trip);

    void deleteImageByHash(String hash);

    Image findByHash(String hash);

    ImageWithoutData findImageWithoutDataByHash(String hash);

    List<ImageWithoutData> findAllWithoutDataByTripTrip(Trip trip);

    Boolean existsImageByHashAndTripTrip(String hash, Trip trip);
}