package travel.way.travelwayapi._core.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseError {
    private String message;
    private int status;
}
