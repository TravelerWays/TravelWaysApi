package travel.ways.travelwaysapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.user.model.db.AppUserTrip;
import travel.ways.travelwaysapi.user.model.db.AppUserTripId;

public interface AppUserTripRepository extends JpaRepository<AppUserTrip, AppUserTripId> {

}
