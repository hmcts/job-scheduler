# Job Scheduler
[![Build Status](https://travis-ci.org/hmcts/job-scheduler.svg?branch=master)](https://travis-ci.org/hmcts/job-scheduler)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/db1d536343474c40967ab9b236044e1d)](https://www.codacy.com/app/HMCTS/job-scheduler)

The job scheduler micro service allows other services to schedule http actions in the future.

## Getting started

### Prerequisites
- [JDK 8](https://java.com)

### Running
Run the application by executing:
```bash
./gradlew bootRun
```

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

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
