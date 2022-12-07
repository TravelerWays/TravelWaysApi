package travel.ways.travelwaysapi._core.model.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;
import travel.ways.travelwaysapi._core.util.TimeUtil;

@Data
public class BaseErrorResponse {
    private String message;
    private HttpStatus status;
    private String timestamp;

    public BaseErrorResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
        this.timestamp = TimeUtil.Now().toString();
    }
}
