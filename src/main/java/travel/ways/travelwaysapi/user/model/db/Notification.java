package travel.ways.travelwaysapi.user.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.enums.NotificationType;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {
    private String hash;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @JoinColumn(nullable = false)
    private String content;

    private String relatedObjectHash;

    @Enumerated(EnumType.ORDINAL)
    private NotificationType type;

    @JoinColumn(nullable = false)
    @Column(columnDefinition = "boolean default false")
    private boolean isRead;
}
