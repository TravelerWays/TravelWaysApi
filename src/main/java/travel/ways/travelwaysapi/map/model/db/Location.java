package travel.ways.travelwaysapi.map.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import travel.ways.travelwaysapi._core.model.db.BaseEntity;
import travel.ways.travelwaysapi.map.model.dto.request.CreateLocationRequest;
import travel.ways.travelwaysapi.trip.model.db.Attraction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "location")
public class Location extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lat", nullable = false)
    private String lat;

    @Column(name = "lon", nullable = false)
    private String lon;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "osm_id", nullable = false, unique = true)
    private String osmId;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = Attraction.class, mappedBy = "location")
    private List<Attraction> attractions = new ArrayList<>();

    public static Location of(CreateLocationRequest createLocationRequest) {
        return new Location(
                createLocationRequest.getName(),
                createLocationRequest.getLat(),
                createLocationRequest.getLon(),
                createLocationRequest.getDisplayName(),
                createLocationRequest.getOsmId(),
                new ArrayList<>()
        );
    }
}
