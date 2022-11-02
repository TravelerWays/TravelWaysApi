package travel.ways.travelwaysapi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi.auth.model.db.RefreshToken;
import travel.ways.travelwaysapi.user.model.db.AppUser;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByToken(String token);

    @Transactional
    @Modifying
    @Query("update RefreshToken r set r.isUsed = true where r.user = ?1")
    void revokeRefresh(AppUser user);

}
