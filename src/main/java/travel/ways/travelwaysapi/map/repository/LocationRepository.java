package travel.ways.travelwaysapi.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.map.model.db.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    boolean existsByOsmId(String osmId);
    Location findByOsmId(String osmId);
}
