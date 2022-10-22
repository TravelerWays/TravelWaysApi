package travel.way.travelwayapi.user.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.way.travelwayapi.user.models.db.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    boolean existsByName(String name);
}
