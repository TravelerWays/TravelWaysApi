package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class CreateTripRequest {
    @NotNull
    private String title;
    @NotNull
    private Boolean isPublic;
    private MultipartFile data;
    private String description;
}
