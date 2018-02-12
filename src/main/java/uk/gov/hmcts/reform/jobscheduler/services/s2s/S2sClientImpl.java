package uk.gov.hmcts.reform.jobscheduler.services.s2s;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class S2sClientImpl implements S2sClient {
    private final RestTemplate restTemplate;
    private final String url;
    private final String secret;
    private final String microService;
    private final ServiceAuthorisationApi serviceAuthorisationApi;


    public S2sClientImpl(
        final String idamUrl,
        final String secret,
        final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        this.restTemplate = new RestTemplate();
        this.url = idamUrl;
        this.secret = secret;
        this.microService = microService;
        this.serviceAuthorisationApi = serviceAuthorisationApi;
    }

    @Override
    public String getServiceName(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, authHeader);

        return restTemplate
            .exchange(
                url + "/details",
                HttpMethod.GET,
                new HttpEntity<String>(headers),
                String.class
            ).getBody();
    }

    @Override
    public AuthTokenGenerator getTokenGenerator() {
        return AuthTokenGeneratorFactory
            .createDefaultGenerator(
                secret,
                microService,
                serviceAuthorisationApi
            );
    }
}
