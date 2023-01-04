package travel.ways.travelwaysapi.file.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddImageToTripRequest {
    @NotNull
    String hash;
    @NotNull
    MultipartFile data;
    @NotNull
    boolean isMain;
}
