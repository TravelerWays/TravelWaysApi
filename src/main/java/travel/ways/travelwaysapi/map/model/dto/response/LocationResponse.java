package travel.ways.travelwaysapi.map.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.map.model.db.Location;
import travel.ways.travelwaysapi.map.model.dto.osm.LocationDto;
import travel.ways.travelwaysapi.trip.model.db.attraction.Attraction;

import java.util.Arrays;
import java.util.Objects;

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
    private Integer rate;
    private String countryCode;

    public static LocationResponse of(Location location) {
        var attractionRates = location.getAttractions().stream().map(Attraction::getRate).filter(Objects::nonNull).mapToInt(Short::intValue).toArray();
        var rate = Arrays.stream(attractionRates).sum();

        return new LocationResponse(
                location.getName(),
                location.getLat(),
                location.getLon(),
                location.getDisplayName(),
                location.getOsmId(),
                rate > 0 ? rate / attractionRates.length : 0,
                location.getCountryCode()
        );
    }

    public static LocationResponse of(LocationDto osmModel) {
        return new LocationResponse(
                osmModel.getDisplayName().split(",")[0],
                osmModel.getLat(),
                osmModel.getLon(),
                osmModel.getDisplayName(),
                osmModel.getOsmId(),
                0,
                osmModel.getCountryCode()
        );
    }
}
