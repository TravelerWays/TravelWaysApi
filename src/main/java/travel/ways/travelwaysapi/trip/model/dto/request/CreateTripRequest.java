package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateTripRequest {
    @NotNull
    private String title;
    @NotNull
    private boolean isPublic;
    private String description;
}
