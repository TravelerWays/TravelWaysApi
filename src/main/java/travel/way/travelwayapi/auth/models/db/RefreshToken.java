package travel.way.travelwayapi.auth.models.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.way.travelwayapi._core.models.db.BaseEntity;
import travel.way.travelwayapi.user.models.db.AppUser;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends BaseEntity {
    private String token;
    private boolean isUsed;

    @ManyToOne
    @JoinColumn(name = "user_Id")
    private AppUser user;
}
