package travel.ways.travelwaysapi.map.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.map.model.db.Location;
import travel.ways.travelwaysapi.map.model.dto.osm.LocationDto;

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

    public static LocationResponse of(LocationDto osmModel) {
        return new LocationResponse(
                osmModel.getDisplayName().split(",")[0],
                osmModel.getLat(),
                osmModel.getLon(),
                osmModel.getDisplayName(),
                osmModel.getOsmId()
        );
    }
}
