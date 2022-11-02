package travel.ways.travelwaysapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.PasswordRecovery;

public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, Long> {
    @Transactional
    @Modifying
    @Query("update PasswordRecovery p set p.isUsed = true where p.user = ?1")
    void setAllRecoveryAsUsed(AppUser user);

    PasswordRecovery findByHash(String hash);

}
