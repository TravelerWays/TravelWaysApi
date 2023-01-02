package travel.ways.travelwaysapi.map.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.map.model.db.Location;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponse {
    private String name;
    private String lat;
    private String lon;
    private String displayName;
    private String osmId;

    public static LocationResponse of(Location location) {
        return new LocationResponse(
                location.getName(),
                location.getLat(),
                location.getLon(),
                location.getDisplayName(),
                location.getOsmId()
        );
    }
}
