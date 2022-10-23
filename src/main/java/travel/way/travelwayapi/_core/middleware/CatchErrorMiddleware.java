package travel.way.travelwayapi._core.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import travel.way.travelwayapi._core.exceptions.ServerException;
import travel.way.travelwayapi._core.models.dto.BaseError;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class CatchErrorMiddleware {

    @ExceptionHandler(ServerException.class)
    @SneakyThrows
    public void serverErrorHandler(ServerException exception, HttpServletResponse response) {
        var mapper = new ObjectMapper();
        var baseError = new BaseError(exception.getMessage(), exception.getStatusCode());
        mapper.writeValue(response.getOutputStream(), baseError);
    }
}
