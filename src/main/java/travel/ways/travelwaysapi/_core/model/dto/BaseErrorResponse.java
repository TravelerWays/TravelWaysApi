package travel.ways.travelwaysapi._core.model.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data

public class BaseErrorResponse {
    private String message;
    private HttpStatus status;
    private String timestamp;
    private String path;

    public BaseErrorResponse(String message, HttpStatus status, String path) {
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now().toString();
        this.path = path;
    }
}
