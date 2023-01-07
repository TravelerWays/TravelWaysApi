package travel.ways.travelwaysapi.trip.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import travel.ways.travelwaysapi.file.model.projection.ImageWithoutData;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripDto {
    private String title;
    private String hash;
    private boolean isPublic;
    private String description;
    private List<ImageWithoutData> images;
    private  boolean isOpen;
}
