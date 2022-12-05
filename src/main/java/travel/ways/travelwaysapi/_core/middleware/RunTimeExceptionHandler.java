package travel.ways.travelwaysapi._core.middleware;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import travel.ways.travelwaysapi._core.model.dto.BaseErrorResponse;

@RestControllerAdvice
@Order
public class RunTimeExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseErrorResponse> runtimeExceptionHandler(RuntimeException exception, WebRequest request) {
        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        var baseError = new BaseErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, path);
        return new ResponseEntity<>(baseError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
