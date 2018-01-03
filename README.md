# Job Scheduler
[![Build Status](https://travis-ci.org/hmcts/job-scheduler.svg?branch=master)](https://travis-ci.org/hmcts/job-scheduler)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/db1d536343474c40967ab9b236044e1d)](https://www.codacy.com/app/HMCTS/job-scheduler)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/db1d536343474c40967ab9b236044e1d)](https://www.codacy.com/app/HMCTS/job-scheduler)

The job scheduler micro service allows other services to schedule http actions in the future. For example, scheduler
can send a POST request to a specified url every Sunday at 2am.

![diagram](docs/diagram.png)

## Getting started

### Prerequisites
- [JDK 8](https://java.com)

### External service dependencies

In order to validate service auth tokens, Job Scheduler sends http requests to S2S Service.  
URL to S2S can be configured via the config file or using environment variables.  
S2S Service is currently not open source.

### Running
Run the application by executing:
```bash
./gradlew bootRun
```

In order to run the application (with its database) in Docker, execute:
```bash
./bin/run-in-docker.sh
```

This script creates a distribution archive for the project, sets up Docker containers
for the application and job database (if those are not set up already) and starts the service.

## API documentation
Api documentation is provided with Swagger:
- json spec: [http://localhost:8484/v2/api-docs](http://localhost:8484/v2/api-docs)
- swagger UI: [http://localhost:8484/swagger-ui.html](http://localhost:8484/swagger-ui.html)

## Developing

### Unit tests
To run all unit tests execute the following command:
```bash
./gradlew test
```

### Code quality checks
We use [checkstyle](http://checkstyle.sourceforge.net/) and [PMD](https://pmd.github.io/).  
To run all checks execute the following command:
```bash
./gradlew check
```

## Job management

The service manages its clients' jobs with [Quartz](http://www.quartz-scheduler.org/).  
It uses a PostgreSQL database for persisting those jobs. Also, Quartz is configured
to run in cluster mode, i.e. the load will be distributed among multiple nodes, each
running different jobs.

## Data security

As of now, job information is stored in an unencrypted form. This means that clients
of this service must not include any sensitive information (tokens, passwords, personally
identifiable information, etc.) in their requests.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
