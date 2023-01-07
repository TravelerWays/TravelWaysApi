package travel.ways.travelwaysapi.auth.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RefreshToken extends BaseEntity {
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private boolean isUsed;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;
}
