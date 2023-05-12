package travel.ways.travelwaysapi.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(AppUser user);

    @Transactional
    @Modifying
    @Query("update Notification n set n.isRead = true where n.user = ?1")
    void markNotificationAsRead(AppUser user);

    @Query("select n from Notification n where n.user = ?1 order by n.isRead, n.createAt desc")
    List<Notification> findUserNotification(AppUser user, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from Notification n where n.relatedObjectHash = ?1")
    int deleteByRelatedObjectHash(String relatedObjectHash);


}
