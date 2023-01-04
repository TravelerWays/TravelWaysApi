package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.Data;

@Data
public class TripDto {
    private String title;
    private String hash;
    private boolean isPublic;
    private String description;
}
