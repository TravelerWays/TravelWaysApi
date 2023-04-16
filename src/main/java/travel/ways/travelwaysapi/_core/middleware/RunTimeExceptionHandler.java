package travel.ways.travelwaysapi._core.middleware;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import travel.ways.travelwaysapi._core.model.dto.BaseErrorResponse;
import travel.ways.travelwaysapi._core.util.Time;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order
public class RunTimeExceptionHandler {
    private final Time time;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseErrorResponse> runtimeExceptionHandler(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        var baseError = new BaseErrorResponse("Internal error", HttpStatus.INTERNAL_SERVER_ERROR, time.now().getTimestamp());
        return new ResponseEntity<>(baseError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<BaseErrorResponse> multipartException(MultipartException exception) {
        if (exception.getMessage() != null) {
            var error = new BaseErrorResponse(exception.getMessage().split(";")[0], HttpStatus.BAD_REQUEST, time.now().getTimestamp());
            return new ResponseEntity<>(error, error.getStatus());
        }
        var error = new BaseErrorResponse("Something went wrong with file upload", HttpStatus.BAD_REQUEST, time.now().getTimestamp());
        return new ResponseEntity<>(error, error.getStatus());
    }
}
