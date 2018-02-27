package uk.gov.hmcts.reform.jobscheduler.controllers;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Configuration
@TestPropertySource("/smoke-application.properties")
public class JobsControllerSmokeTest {

    @Value("${default-test-url}")
    private String testUrl;

    @Before
    public void setup() {
        System.out.println("Test URL " + testUrl);
        RestAssured.baseURI = testUrl;
    }

    @Test
    public void create_jobs_should_require_s2s_auth() {
        RestAssured.given()
            .relaxedHTTPSValidation()
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .when().post("/jobs").then().statusCode(400);
    }

    @Test
    public void retrieve_jobs_should_require_s2s_auth() {
        RestAssured.given()
            .relaxedHTTPSValidation()
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .when().get("/jobs").then().statusCode(400);
    }
}
