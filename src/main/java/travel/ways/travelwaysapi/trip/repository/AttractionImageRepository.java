package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import travel.ways.travelwaysapi.trip.model.db.AttractionImage;
import travel.ways.travelwaysapi.trip.model.db.AttractionImageId;

import java.util.List;

public interface AttractionImageRepository extends JpaRepository<AttractionImage, AttractionImageId> {
    AttractionImage findByImageHash(String oldMainImageHash);

    @Query("select a.image.id from AttractionImage a where a.id.attractionId = ?1")
    List<Long> findAllImageIdInAttraction(Long attractionId);

    @Query("select (count(a) > 0) from AttractionImage a where a.id.imageId = ?1 and a.isMain = true")
    boolean isMain(Long imageId);

    @Query("select (count(a) > 0) from AttractionImage a where a.image.hash = ?1 and a.isMain = true")
    boolean isMain(String imageHash);

    @Query("select (count(a) > 0) from AttractionImage a where a.id.attractionId = ?1 and a.image.hash = ?2")
    boolean existsImageInAttraction(Long attractionId, String hash);


}
