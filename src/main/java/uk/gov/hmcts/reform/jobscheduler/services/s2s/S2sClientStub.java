package uk.gov.hmcts.reform.jobscheduler.services.s2s;

import org.springframework.util.StringUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.jobscheduler.exception.AuthorizationException;

import java.util.Optional;

import static uk.gov.hmcts.reform.jobscheduler.config.ServiceTokenConfiguration.SERVICE_HEADER;

public class S2sClientStub implements S2sClient {

    @Override
    public String getServiceName(String authHeader) {
        return Optional
            .ofNullable(authHeader)
            .filter(token -> !StringUtils.isEmpty(token))
            .orElseThrow(() -> new AuthorizationException(SERVICE_HEADER + " is required"));
    }

    @Override
    public AuthTokenGenerator getTokenGenerator() {
        return new AuthTokenGeneratorStub();
    }
}
