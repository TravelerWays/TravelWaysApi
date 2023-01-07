package travel.ways.travelwaysapi.trip.model.db;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.user.model.db.AppUserTrip;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Trip extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private boolean isPublic;
    @Column(nullable = false)
    private boolean isOpen;
    @Column(unique = true, nullable = false)
    private String hash;
    @Column
    private String description;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @ToString.Exclude
    private Set<AppUserTrip> users = new HashSet<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @ToString.Exclude
    private Set<TripImage> images = new HashSet<>();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<Attraction> attractions = new HashSet<>();

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
