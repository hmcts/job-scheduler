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
import uk.gov.hmcts.reform.jobscheduler.model.HttpAction;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HttpCallJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(HttpCallJob.class);

    private final RestTemplate restTemplate;
    private final ActionExtractor actionExtractor;
    private final AuthTokenGenerator tokenGenerator;

    public HttpCallJob(
        RestTemplate restTemplate,
        ActionExtractor actionExtractor,
        AuthTokenGenerator tokenGenerator
    ) {
        this.restTemplate = restTemplate;
        this.actionExtractor = actionExtractor;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobId = context.getJobDetail().getKey().getName();

        try {
            logger.info("Executing job " + jobId);

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

            logger.info("Job {} executed. Response code: {}", jobId, response.getStatusCodeValue());
        } catch (Exception e) {
            String errorMessage = String.format("Job failed. Job ID: %s", jobId);
            logger.error(errorMessage, e);
            throw new JobExecutionException(errorMessage, e);
        }
    }

    private static HttpEntity toHttpEntity(HttpAction action) {
        HttpHeaders httpHeaders = new HttpHeaders();
        action.headers.forEach(httpHeaders::add);

        return new HttpEntity<>(action.body, httpHeaders);
    }
}
