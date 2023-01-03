package travel.ways.travelwaysapi.trip.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import travel.ways.travelwaysapi.map.service.shared.LocationService;
import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.trip.repository.AttractionRepository;
import travel.ways.travelwaysapi.trip.service.internal.AttractionService;
import travel.ways.travelwaysapi.user.model.db.AppUser;
import travel.ways.travelwaysapi.user.service.shared.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttractionServiceImpl implements AttractionService {
    private final LocationService locationService;
    private final AttractionRepository attractionRepository;
    private final UserService userService;

    @Override
    @Transactional
    public Attraction createAttraction(CreateAttractionRequest createAttractionRequest) {
        var attraction = Attraction.of(createAttractionRequest);
        var location = locationService.getByOsmId(createAttractionRequest.getOsmId());

        attraction.setLocation(location);
        attraction.setUser(userService.getLoggedUser());

        attractionRepository.save(attraction);
        return attraction;
    }

    @Override
    public List<Attraction> getUserAttraction(AppUser user) {
        return attractionRepository.findAllByUser(user);
    }
}
