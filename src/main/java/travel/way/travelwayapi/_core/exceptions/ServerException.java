package travel.way.travelwayapi._core.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerException extends Exception{
    private int statusCode;

    public ServerException(String errorMessage, int statusCode){
        super(errorMessage);
        this.statusCode = statusCode;
    }
}
