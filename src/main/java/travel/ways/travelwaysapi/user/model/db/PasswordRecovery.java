package travel.ways.travelwaysapi.user.model.db;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PasswordRecovery extends BaseEntity {
    @NotNull
    @Column(unique = true, nullable = false, updatable = false)
    private String hash;
    @NotNull
    private boolean isUsed;
    @NotNull
    @Column(unique = false)
    private Timestamp expiredAt;

    @ManyToOne(targetEntity = AppUser.class)
    @JoinColumn(nullable = false, updatable = false)
    private AppUser user;
}
