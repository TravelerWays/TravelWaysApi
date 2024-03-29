package travel.ways.travelwaysapi._core.middleware;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import travel.ways.travelwaysapi._core.exception.ServerException;
import travel.ways.travelwaysapi._core.model.dto.BaseErrorResponse;
import travel.ways.travelwaysapi._core.util.Time;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionsHandler extends ResponseEntityExceptionHandler {
    private final Time time;

    private final String defaultErrorMessage = "Something went wrong";

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<BaseErrorResponse> serverErrorHandler(ServerException exception) {
        log.info(exception.getMessage());
        var baseError = new BaseErrorResponse(exception.getMessage(), exception.getHttpStatus(), time.now().getTimestamp());
        return new ResponseEntity<>(baseError, exception.getHttpStatus());
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleNoHandlerFoundException(@NonNull NoHandlerFoundException exception,
                                                                   @NonNull HttpHeaders headers,
                                                                   @NonNull HttpStatus status,
                                                                   @NonNull WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        var baseError = new BaseErrorResponse("Can not find: " + path, status, time.now().getTimestamp());
        return new ResponseEntity<>(baseError, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(@NonNull HttpRequestMethodNotSupportedException exception,
                                                                         @NonNull HttpHeaders headers,
                                                                         @NonNull HttpStatus status,
                                                                         @NonNull WebRequest request) {
        log.warn(exception.getMessage() + " for " + ((ServletWebRequest) request).getRequest().getRequestURI());
        var baseError = new BaseErrorResponse(defaultErrorMessage, status, time.now().getTimestamp());
        return new ResponseEntity<>(baseError, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        log.warn(exception.getMessage());
        var baseError = new BaseErrorResponse(defaultErrorMessage, status, time.now().getTimestamp());
        return new ResponseEntity<>(baseError, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException exception,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatus status,
                                                                  @NonNull WebRequest request) {
        log.warn(exception.getMessage());
        var baseError = new BaseErrorResponse(defaultErrorMessage, status, time.now().getTimestamp());
        return new ResponseEntity<>(baseError, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(@NonNull HttpMediaTypeNotSupportedException exception,
                                                                     @NonNull HttpHeaders headers,
                                                                     @NonNull HttpStatus status,
                                                                     @NonNull WebRequest request) {
        log.warn(exception.getMessage());
        var baseError = new BaseErrorResponse(defaultErrorMessage, status, time.now().getTimestamp());
        return new ResponseEntity<>(baseError, status);
    }
    

}
