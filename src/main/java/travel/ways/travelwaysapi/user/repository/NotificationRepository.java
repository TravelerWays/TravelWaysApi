package travel.ways.travelwaysapi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(AppUser user);
    
}
