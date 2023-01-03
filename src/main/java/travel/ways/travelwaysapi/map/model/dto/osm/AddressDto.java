package travel.ways.travelwaysapi.map.model.dto.osm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressDto {
    private String city;
    @JsonProperty("city_district")
    private String cityDistrict;
    private String continent;
    private String country;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("house_number")
    private String houseNumber;
    private String postcode;
    @JsonProperty("pubic_building")
    private String pubicBuilding;
}
