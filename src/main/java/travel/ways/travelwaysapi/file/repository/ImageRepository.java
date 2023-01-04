package travel.ways.travelwaysapi.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.file.model.db.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
