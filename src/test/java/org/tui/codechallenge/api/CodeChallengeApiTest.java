package org.tui.codechallenge.api;

import io.restassured.http.ContentType;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.tui.codechallenge.config.TestRestTemplateConfig;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = TestRestTemplateConfig.class)
public class CodeChallengeApiTest {

    @LocalServerPort
    private int port;

    @PostConstruct
    public void init() {
        baseURI = "http://localhost:" + port + "/";
    }

    @Test
    @DisplayName("Get Repositories with non existent user returns 404")
    void whenGetRepositories_returnUserNotFound(){
        String nonExistentUsername = "kjhfsldfhsjdfdsdf";
        String expectedMessage = "GitHub user not found: " + nonExistentUsername;

        given()
                .when()
                .contentType(ContentType.JSON)
                .and()
                .queryParam("username", nonExistentUsername)
                .and()
                .get("/challenge/api/v1/repositories")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .and()
                .assertThat()
                .body("message", equalTo(expectedMessage));

    }


    @Test
    @DisplayName("Get Repositories with  existent user returns 200")
    void whenGetRepositories_returnUserRepoList(){
        given()
                .when()
                .contentType(ContentType.JSON)
                .and()
                .queryParam("username", "teste")
                .and()
                .get("/challenge/api/v1/repositories")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(HttpStatus.OK.value());

    }


}
