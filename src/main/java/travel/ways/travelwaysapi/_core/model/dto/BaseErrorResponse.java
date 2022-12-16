package travel.ways.travelwaysapi._core.model.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

@Data
public class BaseErrorResponse {
    private String message;
    private HttpStatus status;
    private String timestamp;

    public BaseErrorResponse(String message, HttpStatus status, Timestamp timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = Long.toString(timestamp.getTime());
    }
}
