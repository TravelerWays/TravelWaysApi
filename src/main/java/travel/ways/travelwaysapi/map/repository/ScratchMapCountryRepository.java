package travel.ways.travelwaysapi.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.map.model.db.ScratchMapCountry;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface ScratchMapCountryRepository extends JpaRepository<ScratchMapCountry, Long> {
    List<ScratchMapCountry> findAllByUser(AppUser user);
    void deleteAllByUser(AppUser user);
}
