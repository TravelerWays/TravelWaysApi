package travel.ways.travelwaysapi.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.user.model.db.Notification;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationModel {
    private String targetUser;
    private String content;

    public static NotificationModel of(Notification notification) {
        return new NotificationModel(
                notification.getUser().getHash(),
                notification.getContent()
        );
    }
}
