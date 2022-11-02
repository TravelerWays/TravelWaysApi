package travel.ways.travelwaysapi._core.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.dto.BaseErrorResponse;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class CatchErrorMiddleware {

    @ExceptionHandler(ServerException.class)
    @SneakyThrows
    public void serverErrorHandler(ServerException exception, HttpServletResponse response) {
        var mapper = new ObjectMapper();
        var baseError = new BaseErrorResponse(exception.getMessage(), exception.getStatusCode());
        mapper.writeValue(response.getOutputStream(), baseError);
    }
}
