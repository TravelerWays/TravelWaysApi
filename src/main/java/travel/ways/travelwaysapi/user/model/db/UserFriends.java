package travel.ways.travelwaysapi.user.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.enums.FriendsStatus;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class UserFriends extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String hash;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "friend_id", nullable = false)
    private AppUser friend;
    @Enumerated(EnumType.ORDINAL)
    private FriendsStatus status;

    public boolean canEdit(AppUser user) {
        return user.equals(this.user) || user.equals(friend);
    }
}
