package travel.ways.travelwaysapi._core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerException extends Exception{
    private HttpStatus httpStatus;

    public ServerException(String errorMessage, HttpStatus httpStatus){
        super(errorMessage);
        this.httpStatus = httpStatus;
    }
}
