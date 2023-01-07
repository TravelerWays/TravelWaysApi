package travel.ways.travelwaysapi.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import travel.ways.travelwaysapi.map.model.dto.response.VisitedCountriesResponse;
import travel.ways.travelwaysapi.map.service.impl.ScratchMapService;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/scratch-map")
@RequiredArgsConstructor
public class ScratchMapController {
    private final ScratchMapService scratchMapService;

    @GetMapping("/visited-country")
    public VisitedCountriesResponse getVisitedCountries() {
        List<String> visitedCountries = scratchMapService.getVisitedCountries();

        return VisitedCountriesResponse.builder()
                .visitedCountries(visitedCountries)
                .plannedCountries(List.of()).build();
    }

    @PutMapping("/visited-country")
    public void setVisitedCountries(@RequestBody ArrayList<String> countryCodes) {
        scratchMapService.setVisitedCountries(countryCodes);
    }
}
