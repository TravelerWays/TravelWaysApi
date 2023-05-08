package travel.ways.travelwaysapi.user.service.shared;

import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.model.db.Notification;
import travel.ways.travelwaysapi.user.model.dto.NotificationModel;

import java.util.List;

public interface NotificationService {
    void sendNotification(NotificationModel model);

    List<Notification> getUserNotification(AppUser user);
}
