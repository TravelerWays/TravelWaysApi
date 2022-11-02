package travel.ways.travelwaysapi.auth.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.db.AppUser;

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
