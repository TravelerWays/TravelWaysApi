package travel.ways.travelwaysapi.auth.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {
    @Column(name = "token", nullable = false)
    private String token;
    @Column(name = "is_used", nullable = false)
    private boolean isUsed;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
}
