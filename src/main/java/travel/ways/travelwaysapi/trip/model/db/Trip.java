package travel.ways.travelwaysapi.trip.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trip")
public class Trip extends BaseEntity {
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "is_public", nullable = false)
    private boolean isPublic;
    @Column(name = "is_open", nullable = false)
    private boolean isOpen;
    @Column(name = "hash", unique = true, nullable = false)
    private String hash;
    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<AppUserTrip> users = new HashSet<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Set<TripImage> images = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Trip trip = (Trip) o;
        return Objects.equals(hash, trip.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hash);
    }

}
