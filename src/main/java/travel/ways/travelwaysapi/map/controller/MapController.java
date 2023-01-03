package travel.ways.travelwaysapi.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import travel.ways.travelwaysapi.map.model.dto.osm.LocationDto;
import travel.ways.travelwaysapi.map.service.shared.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {
    private final SearchService searchService;

    @GetMapping("/search/query")
    public List<LocationDto> search(@RequestParam String query) {
        return searchService.Search(query);
    }

    @GetMapping("/search/coordinates")
    public List<LocationDto> search(@RequestParam double lat, @RequestParam double lon) {
        return searchService.Search(lat, lon);
    }

}
