package travel.ways.travelwaysapi.file.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class AddImageRequest {
    @NotNull
    MultipartFile imageData;
    @NotNull
    Boolean isMain;
}
