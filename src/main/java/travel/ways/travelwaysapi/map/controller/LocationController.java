package travel.ways.travelwaysapi.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi._core.model.dto.BaseResponse;
import travel.ways.travelwaysapi.map.model.dto.request.CreateLocationRequest;
import travel.ways.travelwaysapi.map.model.dto.response.LocationResponse;
import travel.ways.travelwaysapi.map.service.shared.LocationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/location")
public class LocationController {
    private final LocationService locationService;

    @GetMapping("exists/{osmId}")
    public BaseResponse existsLocation(@PathVariable String osmId) {
        return new BaseResponse(
                locationService.exitsByOsmId(osmId),
                null
        );
    }

    @PostMapping()
    public LocationResponse Add(@RequestBody CreateLocationRequest createLocationRequest){
        return locationService.create(createLocationRequest);
    }

    @PostMapping("add-if-not-exits")
    public LocationResponse AddIfNotExits(@RequestBody CreateLocationRequest createLocationRequest){
        if(!locationService.exitsByOsmId(createLocationRequest.getOsmId())) {
            return locationService.create(createLocationRequest);
        }

        return LocationResponse.of(locationService.getByOsmId(createLocationRequest.getOsmId()));
    }
}
