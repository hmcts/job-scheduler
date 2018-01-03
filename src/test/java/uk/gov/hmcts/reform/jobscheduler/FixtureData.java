package uk.gov.hmcts.reform.jobscheduler;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.authorisation.exceptions.AbstractAuthorisationException;

import java.util.Collections;

public final class FixtureData {

    private FixtureData() {
        // empty constructor
    }

    public static AbstractAuthorisationException getAuthorisationException() {
        feign.Response feignResponse = feign.Response.create(
            HttpStatus.UNAUTHORIZED.value(),
            "i must fail",
            Collections.emptyMap(),
            new byte[0]
        );
        FeignException feignException = FeignException.errorStatus("oh no", feignResponse);

        return AbstractAuthorisationException.parseFeignException(feignException);
    }
}
