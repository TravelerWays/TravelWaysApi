package travel.ways.travelwaysapi._core.middleware;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import travel.ways.travelwaysapi._core.model.dto.BaseErrorResponse;
import travel.ways.travelwaysapi._core.util.Time;

@RestControllerAdvice
@RequiredArgsConstructor
@Order
public class RunTimeExceptionHandler {
    private final Time time;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseErrorResponse> runtimeExceptionHandler() {
        var baseError = new BaseErrorResponse("Internal error", HttpStatus.INTERNAL_SERVER_ERROR, time.now().getTimestamp());
        return new ResponseEntity<>(baseError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
