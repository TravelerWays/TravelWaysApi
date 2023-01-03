package travel.ways.travelwaysapi.trip.service.internal;

import travel.ways.travelwaysapi.trip.model.db.Attraction;
import travel.ways.travelwaysapi.trip.model.dto.request.CreateAttractionRequest;
import travel.ways.travelwaysapi.user.model.db.AppUser;

import java.util.List;

public interface AttractionService {
    Attraction createAttraction(CreateAttractionRequest createAttractionRequest);

    List<Attraction> getUserAttraction(AppUser user);
}
