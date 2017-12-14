package uk.gov.hmcts.reform.jobscheduler;

import feign.FeignException;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.hmcts.reform.jobscheduler.model.errors.FieldError;
import uk.gov.hmcts.reform.jobscheduler.model.errors.ModelValidationError;
import uk.gov.hmcts.reform.jobscheduler.services.auth.AuthException;
import uk.gov.hmcts.reform.jobscheduler.services.jobs.exceptions.JobNotFoundException;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @InitBinder
    protected void activateDirectFieldAccess(DataBinder dataBinder) {
        dataBinder.initDirectFieldAccess();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        List<FieldError> fieldErrors =
            exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> new FieldError(err.getField(), err.getDefaultMessage()))
                .collect(toList());

        return status(HttpStatus.BAD_REQUEST).body(new ModelValidationError(fieldErrors));
    }

    @ExceptionHandler(NotImplementedException.class)
    protected ResponseEntity handleNotImplemented(NotImplementedException exc, WebRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @ExceptionHandler(FeignException.class)
    protected ResponseEntity handleFeignException(FeignException exc, WebRequest req) {
        return status(exc.status()).build();
    }

    @ExceptionHandler(AuthException.class)
    protected ResponseEntity handleAuthException(AuthException exc, WebRequest req) {
        return status(HttpStatus.UNAUTHORIZED).build();
    }

    @ExceptionHandler(JobNotFoundException.class)
    protected ResponseEntity handleNotFoundException(AuthException exc, WebRequest req) {
        return status(HttpStatus.NOT_FOUND).build();
    }
}
