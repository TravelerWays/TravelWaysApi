package travel.ways.travelwaysapi._core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseErrorResponse {
    private String message;
    private int status;
}
