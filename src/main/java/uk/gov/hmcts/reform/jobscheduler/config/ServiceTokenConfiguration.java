package uk.gov.hmcts.reform.jobscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.validators.ServiceAuthTokenValidator;
import uk.gov.hmcts.reform.jobscheduler.services.s2s.S2sClient;
import uk.gov.hmcts.reform.jobscheduler.services.s2s.S2sClientImpl;
import uk.gov.hmcts.reform.jobscheduler.services.s2s.S2sClientStub;

@Configuration
@Lazy
@EnableFeignClients(basePackageClasses = ServiceAuthorisationApi.class)
public class ServiceTokenConfiguration {

    public static final String SERVICE_HEADER = "ServiceAuthorization";

    @Bean
    public ServiceAuthTokenValidator tokenValidator(
        final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        return new ServiceAuthTokenValidator(serviceAuthorisationApi);
    }

    @Bean
    @ConditionalOnProperty(name = "idam.s2s-auth.useStub", havingValue = "false")
    public S2sClient s2sClient(
        @Value("${s2s.url}") final String s2sUrl,
        @Value("${idam.s2s-auth.secret}") final String secret,
        @Value("${idam.s2s-auth.microservice}") final String microService,
        final ServiceAuthorisationApi serviceAuthorisationApi
    ) {
        return new S2sClientImpl(s2sUrl, secret, microService, serviceAuthorisationApi);
    }

    @Bean
    @ConditionalOnProperty(name = "idam.s2s-auth.useStub", havingValue = "true")
    public S2sClient s2sClientStub() {
        return new S2sClientStub();
    }
}
