package travel.ways.travelwaysapi.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.projection.ImageSummary;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Long> {

    ImageSummary findImageSummaryByTripTripAndTripIsMainTrue(Trip trip);

    ImageSummary findImageSummaryByAttractionAttractionAndAttractionIsMainTrue(Attraction attraction);

    void deleteImageByHash(String hash);

    Image findByHash(String hash);

    ImageSummary findImageSummaryByHash(String hash);

    List<ImageSummary> findAllImageSummaryByTripTrip(Trip trip);

    Boolean existsImageByHashAndTripTrip(String hash, Trip trip);

    Boolean existsImageByHashAndAttractionAttraction(String hash, Attraction attraction);

    Boolean existsImageByHashAndAttractionIsNotNull(String hash);

    Boolean existsImageByHashAndTripIsNotNull(String hash);

    List<ImageSummary> findAllImageSummaryByAttractionAttraction(Attraction attraction);
}
