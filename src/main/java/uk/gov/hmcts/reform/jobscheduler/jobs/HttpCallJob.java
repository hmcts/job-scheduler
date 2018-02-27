package uk.gov.hmcts.reform.jobscheduler.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.jobscheduler.logging.AppInsights;
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;

import java.time.Duration;
import java.time.Instant;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HttpCallJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(HttpCallJob.class);

    private final RestTemplate restTemplate;
    private final ActionExtractor actionExtractor;
    private final AuthTokenGenerator tokenGenerator;
    private final AppInsights insights;

    public HttpCallJob(
        RestTemplate restTemplate,
        ActionExtractor actionExtractor,
        AuthTokenGenerator tokenGenerator,
        AppInsights insights
    ) {
        this.restTemplate = restTemplate;
        this.actionExtractor = actionExtractor;
        this.tokenGenerator = tokenGenerator;
        this.insights = insights;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobId = context.getJobDetail().getKey().getName();

        logger.info("Executing job {}", jobId);

        Instant start = Instant.now();

        try {
            HttpAction action = actionExtractor.extract(context)
                .withHeader("ServiceAuthorization", tokenGenerator.generate());

            ResponseEntity<String> response =
                restTemplate
                    .exchange(
                        action.url,
                        action.method,
                        toHttpEntity(action),
                        String.class
                    );

            insights.trackHttpCallJobExecution(Duration.between(start, Instant.now()), true);

            logger.info("Job {} executed. Response code: {}", jobId, response.getStatusCodeValue());
        } catch (Exception e) {
            insights.trackHttpCallJobExecution(Duration.between(start, Instant.now()), false);

            String errorMessage = String.format("Job failed. Job ID: %s", jobId);
            logger.error(errorMessage, e);

            throw new JobExecutionException(errorMessage, e);
        }
    }

    private static HttpEntity<String> toHttpEntity(HttpAction action) {
        HttpHeaders httpHeaders = new HttpHeaders();
        action.headers.forEach(httpHeaders::add);

        return new HttpEntity<>(action.body, httpHeaders);
    }
}
