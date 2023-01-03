package travel.ways.travelwaysapi.map.service.shared;

import travel.ways.travelwaysapi.map.model.db.Location;
import travel.ways.travelwaysapi.map.model.dto.request.CreateLocationRequest;
import travel.ways.travelwaysapi.map.model.dto.response.LocationResponse;

public interface LocationService {
    Location getLocation(Long id);

    Location getByOsmId(String osmId);

    boolean exitsByOsmId(String osmId);

    LocationResponse create(CreateLocationRequest createLocationRequest);
}
