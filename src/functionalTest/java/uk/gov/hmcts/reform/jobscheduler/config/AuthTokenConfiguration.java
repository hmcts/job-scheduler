package uk.gov.hmcts.reform.jobscheduler.config;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;

import java.util.List;

@Configuration
public class AuthTokenConfiguration {

    @Bean
    @ConditionalOnProperty(name = "idam.s2s-auth.url", havingValue = "false")
    public AuthTokenGenerator tokenGeneratorStub() {
        return () -> "newly-generated-token";
    }

    @Bean
    @ConditionalOnProperty(name = "idam.s2s-auth.url", havingValue = "false")
    public AuthTokenValidator tokenValidatorStub() {
        return new AuthTokenValidator() {
            public void validate(String token) {
                throw new NotImplementedException();
            }

            public void validate(String token, List<String> roles) {
                throw new NotImplementedException();
            }

            public String getServiceName(String token) {
                return "some_service_name";
            }
        };
    }
}
