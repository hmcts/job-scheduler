package uk.gov.hmcts.reform.jobscheduler.contollers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.services.JobsService;
import uk.gov.hmcts.reform.jobscheduler.services.S2sClient;

import java.net.URI;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/jobs")
public class JobsController {

    private final JobsService jobsService;
    private final S2sClient s2sClient;

    public JobsController(JobsService jobsService, S2sClient s2sClient) {
        this.jobsService = jobsService;
        this.s2sClient = s2sClient;
    }

    @PostMapping(path = "")
    public ResponseEntity<Void> create(
        @RequestBody Job job,
        @RequestHeader("ServiceAuthorization") String serviceAuthHeader
    ) {
        String serviceName = s2sClient.getServiceName(serviceAuthHeader);
        String id = this.jobsService.create(job, serviceName);

        URI newJobUri = fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

        return created(newJobUri).build();
    }
}
