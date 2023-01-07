package travel.ways.travelwaysapi.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import travel.ways.travelwaysapi.map.model.db.ScratchMapCountry;
import travel.ways.travelwaysapi.map.model.dto.response.VisitedCountriesResponse;
import travel.ways.travelwaysapi.map.repository.ScratchMapCountryRepository;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/scratch-map")
@RequiredArgsConstructor
public class ScratchMapController {
    private final ScratchMapCountryRepository scratchMapCountryRepository;

    private final UserService userService;

    @GetMapping("/visited-country")
    public VisitedCountriesResponse getVisitedCountries() {
        AppUser user = userService.getLoggedUser();
        List<String> visitedCountries = scratchMapCountryRepository.findAllByUser(user).stream()
                .map(ScratchMapCountry::getCountryCode)
                .toList();
        return VisitedCountriesResponse.builder()
                .visitedCountries(visitedCountries)
                .plannedCountries(List.of("GB", "MX")).build();
    }

    @PutMapping("/visited-country")
    @Transactional
    public void setVisitedCountries(@RequestBody ArrayList<String> countryCodes) {
        AppUser user = userService.getLoggedUser();
        scratchMapCountryRepository.deleteAllByUser(user);
        countryCodes.stream()
                .map((code) -> new ScratchMapCountry(code, user))
                .forEach(scratchMapCountryRepository::save);
    }
}
