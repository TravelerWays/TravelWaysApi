package travel.ways.travelwaysapi.map.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi.map.model.db.Location;
import travel.ways.travelwaysapi.map.model.dto.request.CreateLocationRequest;
import travel.ways.travelwaysapi.map.model.dto.response.LocationResponse;
import travel.ways.travelwaysapi.map.repository.LocationRepository;
import travel.ways.travelwaysapi.map.service.shared.LocationService;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;


    @Override
    @SneakyThrows
    public Location getByOsmId(String osmId) {
        var location = locationRepository.findByOsmId(osmId);
        if (location == null) {
            throw new ServerException("Location not found", HttpStatus.NOT_FOUND);
        }

        return location;
    }

    @Override
    public boolean exitsByOsmId(String osmId) {
        return locationRepository.existsByOsmId(osmId);
    }

    @Override
    @SneakyThrows
    public LocationResponse create(CreateLocationRequest createLocationRequest) {
        if (locationRepository.existsByOsmId(createLocationRequest.getOsmId())) {
            throw new ServerException("location with this osm exits", HttpStatus.CONFLICT);
        }
        var location = Location.of(createLocationRequest);
        locationRepository.save(location);
        return LocationResponse.of(location);
    }
}
