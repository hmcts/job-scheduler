package uk.gov.hmcts.reform.jobscheduler.services.s2s;

import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

public class AuthTokenGeneratorStub implements AuthTokenGenerator {

    public String generate() {
        return "123456";
    }
}
