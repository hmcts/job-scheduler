package uk.gov.hmcts.reform.jobscheduler.controllers;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class JobsControllerSmokeTest {

    @Before
    public void setup() throws IOException {
        Map<String, String> env = System.getenv();
        if (env.get("TEST_URL") == null) {
            Resource resource = new ClassPathResource("/smoke-application.properties");
            Properties props = PropertiesLoaderUtils.loadProperties(resource);
            RestAssured.baseURI = props.getProperty("default-test-url");
        } else {
            RestAssured.baseURI = env.get("TEST_URL");
        }
    }

    @Test
    public void create_jobs_should_require_s2s_auth() {
        RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .when().post("/jobs").then().statusCode(400);
    }

    @Test
    public void retrieve_jobs_should_require_s2s_auth() {
        RestAssured.given()
            .header(HttpHeaders.CONTENT_TYPE, "application/json")
            .when().get("/jobs").then().statusCode(400);
    }
}
