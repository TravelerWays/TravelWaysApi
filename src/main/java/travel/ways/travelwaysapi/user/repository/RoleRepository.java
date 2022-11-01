package travel.ways.travelwaysapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.user.model.db.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    boolean existsByName(String name);
}
