package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.trip.model.db.AttractionImage;
import travel.ways.travelwaysapi.trip.model.db.AttractionImageId;

public interface AttractionImageRepository extends JpaRepository<AttractionImage, AttractionImageId> {
    AttractionImage findByImageHash(String oldMainImageHash);
}
