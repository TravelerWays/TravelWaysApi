package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditTripMainImageRequest {
    @NotNull
    private String tripHash;
    private String imageHash;
}
