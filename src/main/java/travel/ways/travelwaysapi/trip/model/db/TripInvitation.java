package travel.ways.travelwaysapi.trip.model.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripInvitation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @Column(nullable = false)
    private Boolean active = true;
    @Column(nullable = false)
    private Boolean accepted = false;
    @Column(unique = true, nullable = false)
    private String hash = UUID.randomUUID().toString();

    public TripInvitation(Trip trip, AppUser user) {
        this.trip = trip;
        this.user = user;
    }
}
