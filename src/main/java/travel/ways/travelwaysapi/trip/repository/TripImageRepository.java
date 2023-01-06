package travel.ways.travelwaysapi.trip.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.trip.model.db.TripImage;
import travel.ways.travelwaysapi.trip.model.db.TripImageId;

public interface TripImageRepository extends JpaRepository<TripImage, TripImageId> {

    TripImage findByImageHash(String imageHash);
}
