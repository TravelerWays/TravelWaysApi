package travel.way.travelwayapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.way.travelwayapi.user.models.db.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
