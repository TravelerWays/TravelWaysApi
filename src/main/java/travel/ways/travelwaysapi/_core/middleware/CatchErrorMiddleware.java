package travel.ways.travelwaysapi._core.middleware;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.dto.BaseErrorResponse;

@RestControllerAdvice
public class CatchErrorMiddleware extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<BaseErrorResponse> serverErrorHandler(ServerException exception, WebRequest request) {
        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        var baseError = new BaseErrorResponse(exception.getMessage(), exception.getHttpStatus(), path);
        return new ResponseEntity<>(baseError, exception.getHttpStatus());
    }

}
