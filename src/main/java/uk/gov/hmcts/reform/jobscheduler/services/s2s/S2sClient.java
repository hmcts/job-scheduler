package uk.gov.hmcts.reform.jobscheduler.services.s2s;

import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

public interface S2sClient {
    String getServiceName(String authHeader);

    AuthTokenGenerator getTokenGenerator();
}
