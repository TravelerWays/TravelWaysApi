package travel.ways.travelwaysapi.trip.model.db.attraction;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.map.model.db.Location;
import travel.ways.travelwaysapi.trip.model.db.trip.Trip;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Attraction extends BaseEntity {
    @Column(nullable = false)
    private String title;
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private boolean isVisited;

    @Temporal(TemporalType.TIMESTAMP)
    @Column()
    private Date visitedAt;

    @Column
    private Short rate;

    @Column(nullable = false, unique = true)
    private String hash;

    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @OneToMany(mappedBy = "attraction", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @ToString.Exclude
    private Set<AttractionImage> images = new HashSet<>();

    public Attraction(String hash, String title, String description, boolean isPublic, boolean isVisited, Date visitedAt, Short rate) {
        this.hash = hash;
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
        this.isVisited = isVisited;
        this.visitedAt = visitedAt;
        this.rate = rate;
    }

    public static Attraction of(CreateAttractionRequest createAttractionRequest) {
        return new Attraction(
                UUID.randomUUID().toString(),
                createAttractionRequest.getTitle(),
                createAttractionRequest.getDescription(),
                createAttractionRequest.isPublic(),
                createAttractionRequest.isVisited(),
                createAttractionRequest.getVisitedAt(),
                createAttractionRequest.getRate()
        );
    }
}
