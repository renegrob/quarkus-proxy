package io.github.renegrob.quarkus.proxy;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

@QuarkusTest
public class StaticResourceTest {

    @Test
    public void testStaticFile() {
        given()
                .when().get("/static/some-file.txt")
                .then()
                .statusCode(200)
                .body(startsWith("Hello from static text file!"));
    }

    @Test
    public void testStaticIndex() {
        given()
                .when().get("/static/")
                .then()
                .statusCode(200)
                .body(startsWith("Hello from index.html!"));
    }

    @Test
    public void testStaticIndexNoTrailingSlash() {
        given()
                .when().get("/static")
                .then()
                .statusCode(200)
                .body(startsWith("Hello from index.html!"));
    }

    @Test
    public void testStaticNotFound() {
        given()
                .when().get("/static/inexistent.txt")
                .then()
                .statusCode(404)
                .body(containsString("NOT FOUND"));
    }
}
