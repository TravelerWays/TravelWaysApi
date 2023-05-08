package travel.ways.travelwaysapi.user.model.enums;

import lombok.Getter;

@Getter
public enum FriendsStatus {
    None(0),
    Pending(1),
    Accepted(2),
    Reject(3);


    private final int value;

    FriendsStatus(int value) {
        this.value = value;
    }
}
