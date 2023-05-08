package travel.ways.travelwaysapi.user.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    @JoinColumn(nullable = false)
    @Column(columnDefinition = "boolean default false")
    private boolean isRead;
}
