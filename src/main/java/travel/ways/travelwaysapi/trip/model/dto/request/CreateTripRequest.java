package travel.ways.travelwaysapi.trip.model.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateTripRequest {
    private String title;
    private String isPublic;
    private MultipartFile data;
}
