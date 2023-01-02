package travel.ways.travelwaysapi.map.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLocationRequest {
    private String name;
    private String lat;
    private String lon;
    private String displayName;
    private String osmId;
}
