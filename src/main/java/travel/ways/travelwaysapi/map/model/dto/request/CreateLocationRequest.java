package travel.ways.travelwaysapi.map.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocationRequest {
    private String name;
    private String lat;
    private String lon;
    private String displayName;
    private String osmId;
}
