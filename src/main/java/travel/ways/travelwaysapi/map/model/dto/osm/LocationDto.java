package travel.ways.travelwaysapi.map.model.dto.osm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocationDto {
    private AddressDto address;
    @JsonProperty("display_name")
    private String displayName;
    private String lat;
    private String lon;
    private String type;
    @JsonProperty("osm_id")
    private String OsmId;
}
