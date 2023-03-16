package travel.ways.travelwaysapi.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import travel.ways.travelwaysapi.file.model.db.Image;
import travel.ways.travelwaysapi.file.model.projection.ImageSummary;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.db.Trip;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface ImageRepository extends JpaRepository<Image, Long> {

    ImageSummary findImageSummaryByTripTripAndTripIsMainTrue(Trip trip);

    ImageSummary findImageSummaryByAttractionAttractionAndAttractionIsMainTrue(Attraction attraction);

    @Query("select i from Image i where i.user = ?1")
    ImageSummary findImageSummaryByUserIs(AppUser user);

    void deleteImageByHash(String hash);

    Optional<Image> findByHash(String hash);

    ImageSummary findImageSummaryByHash(String hash);

    ImageSummary findImageSummaryById(Long id);

    List<ImageSummary> getAllByIdIn(Collection<Long> id);
}
