package travel.ways.travelwaysapi.map.service.shared;

import travel.ways.travelwaysapi.map.model.dto.LocationDto;

import java.util.List;

public interface SearchService {
    List<LocationDto> Search(String query);
    List<LocationDto> Search(double lat, double lon);
}
