package uk.gov.hmcts.reform.jobscheduler.services.auth;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;

@Service
public class AuthService {

    private final ServiceAuthTokenValidator tokenValidator;

    public AuthService(ServiceAuthTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    /**
     * Authenticates the service.
     * Returns service name on success.
     */
    public String authenticate(String serviceAuthHeader) {
        return tokenValidator.getServiceName(serviceAuthHeader);
    }
}
