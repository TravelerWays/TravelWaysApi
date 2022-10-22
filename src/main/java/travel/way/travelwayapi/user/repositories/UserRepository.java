package travel.way.travelwayapi.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.way.travelwayapi.user.models.db.AppUser;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
