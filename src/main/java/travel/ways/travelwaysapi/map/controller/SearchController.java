package travel.ways.travelwaysapi.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import travel.ways.travelwaysapi.map.model.dto.LocationDto;
import travel.ways.travelwaysapi.map.service.shared.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("search/query")
    public List<LocationDto> Search(@RequestParam String query){
        return searchService.Search(query);
    }

    @GetMapping("search/coordinates")
    public List<LocationDto> Search(@RequestParam double lat, @RequestParam double lon){
        return searchService.Search(lat, lon);
    }

}
