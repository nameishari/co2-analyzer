package org.demo.co2analyzer.service;

import org.demo.co2analyzer.api.dto.request.MeasurementRequest;
import org.demo.co2analyzer.api.dto.request.SensorRequest;
import org.demo.co2analyzer.api.dto.response.SensorResponse;
import org.demo.co2analyzer.config.PostgresContainer;
import org.demo.co2analyzer.entity.AlertHistory;
import org.demo.co2analyzer.entity.Measurement;
import org.demo.co2analyzer.entity.Sensor;
import org.demo.co2analyzer.entity.SensorStatus;
import org.demo.co2analyzer.repository.AlertHistoryRepository;
import org.demo.co2analyzer.repository.MeasurementRepository;
import org.demo.co2analyzer.repository.SensorRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/sql/delete-test-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@ActiveProfiles({"integration-test"})
public class SensorServiceTest {
    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private AlertHistoryRepository alertHistoryRepository;

    @Autowired
    private SensorService underTest;

    @Container
    public static PostgreSQLContainer<PostgresContainer> postgreSQLContainer = PostgresContainer.getInstance();

    @ParameterizedTest
    @MethodSource("addMeasurementTestData")
    public void testShouldSaveMeasurementsAndUpdateSensorStatus(List<Integer> readings, SensorStatus expectedSensorStatus, int expectedAlertsHistorySize, boolean havenAnOpenAlertInAlertHistory) {
        // Given
        SensorResponse sensor = createSensor();
        OffsetDateTime baseDateTime = OffsetDateTime.now().minusDays(1);

        // When
        for (int i = 0; i < readings.size(); i++) {
            MeasurementRequest request = new MeasurementRequest(readings.get(i), baseDateTime.plusMinutes(i));
            underTest.addMeasurement(sensor.id(), request);
        }

        // Then
        Sensor sensorUpdated = sensorRepository.findById(sensor.id()).get();
        assertThat(sensorUpdated.getStatus(), equalTo(expectedSensorStatus));
        List<Integer> actualReadings = measurementRepository.findAllBySensorId(sensor.id())
                .stream()
                .sorted(Comparator.comparing(Measurement::getTime))
                .map(Measurement::getReading)
                .toList();
        assertThat(actualReadings.equals(readings), equalTo(true));
        if (expectedAlertsHistorySize > 0) {
            assertAlertsHistory(sensor.id(), expectedAlertsHistorySize, havenAnOpenAlertInAlertHistory);
        }
    }

    private void assertAlertsHistory(UUID sensorId, int expectedAlertsHistorySize, boolean havenAnOpenAlertInAlertHistory) {
        List<AlertHistory> alertHistory = alertHistoryRepository.findAllBySensorId(sensorId);
        assertThat(alertHistory, hasSize(expectedAlertsHistorySize));
        if (!havenAnOpenAlertInAlertHistory) {
            assertThat(alertHistory.stream().allMatch(a -> a.getEndTime() != null), is(true));
        } else {
            alertHistory.sort(Comparator.comparing(AlertHistory::getStartTime, Comparator.nullsLast(Comparator.reverseOrder())));
            assertThat(alertHistory.get(0).getEndTime(), is(nullValue()));
        }
    }

    private SensorResponse createSensor() {
        return underTest.create(new SensorRequest("name", "location"));
    }

    private static Stream<Arguments> addMeasurementTestData() {
        // Test data format: readings, expected sensor status, expected number of alerts in alert history, have an open alert
        return Stream.of(
                Arguments.of(List.of(1000), SensorStatus.OK, 0, false),
                Arguments.of(List.of(2000), SensorStatus.WARN, 0, false),
                Arguments.of(List.of(1000, 2000, 1000, 2000, 1000), SensorStatus.OK, 0, false),
                Arguments.of(List.of(2000, 1000, 2000), SensorStatus.WARN, 0, false),
                Arguments.of(List.of(2000, 1000, 2000, 2000, 2000), SensorStatus.ALERT, 1, true),
                Arguments.of(List.of(2000, 1000, 2000, 2000, 2000, 1000, 1000), SensorStatus.ALERT, 1, true),
                Arguments.of(List.of(2000, 1000, 2000, 2000, 2000, 1000, 1000, 1000), SensorStatus.OK, 1, false),
                Arguments.of(List.of(2000, 2000, 2000, 1000, 1000, 1000, 2003), SensorStatus.WARN, 1, false),
                Arguments.of(List.of(2000, 2000, 2000, 1000, 1000, 1000, 2000, 2000, 2005), SensorStatus.ALERT, 2, true),
                Arguments.of(List.of(2000, 2000, 2000, 1000, 1000, 1000, 2000, 2000, 2005, 1000, 1000, 1000), SensorStatus.OK, 2, false)
        );
    }

}
