package org.jboss.unimbus.health;

import java.net.URL;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jboss.unimbus.servlet.Management;
import org.jboss.unimbus.test.UNimbusTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.when;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(UNimbusTestRunner.class)
public class EndpointTest {

    @Before
    public void setup() {
        RestAssured.baseURI = this.url.toExternalForm();
    }

    @After
    public void teardown() {
        RestAssured.reset();
    }

    @Test
    public void test() {
        Response response = when().get("/health").andReturn();

        JsonReader reader = Json.createReader(response.asInputStream());
        JsonObject root = reader.readObject();

        assertThat(root.getString("outcome")).isEqualTo("UP");

        JsonArray checks = root.getJsonArray("checks");
        assertThat(checks.size()).isGreaterThan(0);

        checks.forEach(check -> {
            assertThat(((JsonObject) check).getString("state")).isEqualTo("UP");
        });

        //System.err.println( response.asString() );
        /*
        when().get("/health").then()
                .statusCode(200)
                .body(
                        containsString("{\"name\":\"undertow-management\",\"state\":\"UP\"}")
                );
                */
    }

    @Inject
    @Management
    URL url;
}
