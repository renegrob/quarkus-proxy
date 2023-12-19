package io.github.renegrob.quarkus.proxy;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class ProxyRouteTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/users")
                .then()
                .statusCode(200);
    }
}
