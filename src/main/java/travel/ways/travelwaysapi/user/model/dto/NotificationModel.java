package travel.ways.travelwaysapi.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.user.model.db.Notification;
import travel.ways.travelwaysapi.user.model.enums.NotificationType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationModel {
    private String targetUser;
    private String content;
    private String relatedObjectHash;
    private boolean isRead;
    private NotificationType type;

    public static NotificationModel of(Notification notification) {
        return new NotificationModel(
                notification.getUser().getHash(),
                notification.getContent(),
                notification.getRelatedObjectHash(),
                notification.isRead(),
                notification.getType()
        );
    }
}
