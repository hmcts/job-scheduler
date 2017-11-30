package uk.gov.hmcts.reform.jobscheduler.contollers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.jobscheduler.model.Job;
import uk.gov.hmcts.reform.jobscheduler.services.JobsService;

import java.net.URI;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/jobs")
public class JobsController {

    private final JobsService jobsService;

    public JobsController(JobsService jobsService) {
        this.jobsService = jobsService;
    }

    @PostMapping(path = "")
    public ResponseEntity<Void> create(@RequestBody Job job) {
        String id = this.jobsService.create(job);
        URI newJobUri = fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

        return created(newJobUri).build();
    }
}
