package travel.ways.travelwaysapi.user.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppUserTripId implements Serializable {
    private long tripId;
    private long appUserId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUserTripId that = (AppUserTripId) o;
        return tripId == that.tripId && appUserId == that.appUserId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, appUserId);
    }
}
