package travel.ways.travelwaysapi.user.model.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    NewInvitation(0);


    private final int value;

    NotificationType(int value) {
        this.value = value;
    }

}
