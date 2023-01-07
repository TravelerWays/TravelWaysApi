package travel.ways.travelwaysapi.map.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import travel.ways.travelwaysapi.map.model.db.ScratchMapCountry;
import travel.ways.travelwaysapi.map.repository.ScratchMapCountryRepository;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ScratchMapService {

    private final ScratchMapCountryRepository scratchMapCountryRepository;

    private final UserService userService;

    public List<String> getVisitedCountries() {
        AppUser user = userService.getLoggedUser();
        return scratchMapCountryRepository.findAllByUser(user).stream()
                .map(ScratchMapCountry::getCountryCode)
                .toList();
    }

    @Transactional
    public void setVisitedCountries(ArrayList<String> countryCodes) {
        AppUser user = userService.getLoggedUser();

        List<String> alreadyPresent = new ArrayList<>();
        scratchMapCountryRepository.findAllByUser(user)
                .forEach((visitedCountry) -> {
                    if (!countryCodes.contains(visitedCountry.getCountryCode())) {
                        scratchMapCountryRepository.delete(visitedCountry);
                    } else {
                        alreadyPresent.add(visitedCountry.getCountryCode());
                    }
                });

        countryCodes.stream()
                .filter((code) -> !alreadyPresent.contains(code))
                .map((code) -> new ScratchMapCountry(code, user))
                .forEach(scratchMapCountryRepository::save);
    }
}
