package uk.gov.hmcts.reform.jobscheduler;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.ResponseEntity.status;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotImplementedException.class)
    protected ResponseEntity handleNotImplemented(NotImplementedException exc, WebRequest req) {
        return status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
