package travel.ways.travelwaysapi.trip.model.db;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "app_user_trip")
public class AppUserTrip {
    @EmbeddedId
    private AppUserTripId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tripId")
    @JsonManagedReference
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("appUserId")
    @JsonManagedReference
    private AppUser user;
    @Column(name = "is_owner")
    private boolean isOwner;

    public AppUserTrip() {
    }

    public AppUserTrip(Trip trip, AppUser appUser) {
        this.trip = trip;
        this.user = appUser;
        this.isOwner = false;
        this.id = new AppUserTripId(trip.getId(), appUser.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUserTrip that = (AppUserTrip) o;
        return Objects.equals(trip, that.trip) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trip, user);
    }
}
