package uk.gov.hmcts.reform.jobscheduler.services.auth;

import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final S2sClient s2sClient;

    public AuthService(S2sClient s2sClient) {
        this.s2sClient = s2sClient;
    }

    /**
     * Authenticates the service.
     * Returns service name on success.
     */
    public String authenticate(String serviceAuthHeader) {
        try {
            return s2sClient.getServiceName(serviceAuthHeader);
        } catch (FeignException exc) {
            boolean isClientError = exc.status() >= 400 && exc.status() <= 499;

            throw isClientError ? new AuthException(exc.getMessage(), exc) : exc;
        }
    }
}
