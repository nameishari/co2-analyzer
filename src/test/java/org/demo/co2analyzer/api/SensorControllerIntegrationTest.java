package org.demo.co2analyzer.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.demo.co2analyzer.api.dto.request.MeasurementRequest;
import org.demo.co2analyzer.api.dto.request.SensorRequest;
import org.demo.co2analyzer.config.PostgresContainer;
import org.demo.co2analyzer.entity.Sensor;
import org.demo.co2analyzer.entity.SensorStatus;
import org.demo.co2analyzer.repository.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/sql/delete-test-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/sql/insert-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@ActiveProfiles({"integration-test"})
class SensorControllerIntegrationTest {
    private static final UUID TEST_SENSOR_UUID = UUID.fromString("c7beaf7c-8bbf-4b1a-9ecd-f1973ef7e103");
    private static final String SENSOR_BASE_URL = "/sensors";

    @Autowired
    private SensorRepository sensorRepository;

    @Container
    public static PostgreSQLContainer<PostgresContainer> postgreSQLContainer = PostgresContainer.getInstance();

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    public void testShouldPostSensor() {
        var sensorRequest = new SensorRequest("Sensor Name", "Charlottenburg palace, Berlin");
        //@formatter:off
        UUID id = given()
                    .body(sensorRequest)
                    .contentType(ContentType.JSON)
                  .when()
                    .post(SENSOR_BASE_URL)
                    .prettyPeek()
                  .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("id", is(notNullValue()))
                    .body("name", is(sensorRequest.name()))
                    .body("location", is(sensorRequest.location()))
                    .extract()
                    .jsonPath()
                    .getUUID("id");
        //@formatter:on

        Optional<Sensor> optionalSensor = sensorRepository.findById(id);
        assertThat(optionalSensor.isPresent(), is(true));
        assertThat(optionalSensor.get().getId(), is(equalTo(id)));
        assertThat(optionalSensor.get().getName(), is(equalTo(sensorRequest.name())));
        assertThat(optionalSensor.get().getLocation(), is(equalTo(sensorRequest.location())));
        assertThat(optionalSensor.get().getStatus(), is(nullValue()));
    }

    @Test
    public void testShouldSaveMeasurementAndGetSensorStatus() {
        var measurementRequest = new MeasurementRequest(4000, OffsetDateTime.now().minusDays(1));
        //@formatter:off
        given()
            .pathParam("sensorId", TEST_SENSOR_UUID)
            .body(measurementRequest)
            .contentType(ContentType.JSON)
        .when()
            .post(SENSOR_BASE_URL + "/{sensorId}/measurement")
            .prettyPeek()
        .then()
             .statusCode(HttpStatus.CREATED.value())
             .body("id", is(notNullValue()))
             .body("sensorId", equalTo(TEST_SENSOR_UUID.toString()))
             .body("reading", equalTo(measurementRequest.reading()))
             .body("time", is(notNullValue()));

        given()
            .pathParam("sensorId", TEST_SENSOR_UUID)
        .when()
            .get(SENSOR_BASE_URL + "/{sensorId}")
            .prettyPeek()
        .then()
             .statusCode(HttpStatus.OK.value())
             .body("id", equalTo(TEST_SENSOR_UUID.toString()))
             .body("status", equalTo(SensorStatus.WARN.name()));
        //@formatter:on
    }

    @Test
    @Sql(scripts = {"/sql/insert-test-data.sql", "/sql/insert-measurement-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testShouldGetMetrics() {
        //@formatter:off
        given()
            .pathParam("sensorId", TEST_SENSOR_UUID)
        .when()
            .get(SENSOR_BASE_URL + "/{sensorId}/metrics")
            .prettyPeek()
        .then()
            .statusCode(HttpStatus.OK.value())
            .body("maxLast30Days", equalTo(2003.0F))
            .body("avgLast30Days", equalTo((float)(1000+1450+1560+1990+2003) / 5));
        //@formatter:on

    }
}
