package io.github.renegrob.quarkus.proxy;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class RewriteProxyRouteTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/geoip")
                .then()
                .statusCode(200);
    }

}
