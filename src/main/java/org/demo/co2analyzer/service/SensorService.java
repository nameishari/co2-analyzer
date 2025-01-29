package org.demo.co2analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.co2analyzer.api.dto.request.MeasurementRequest;
import org.demo.co2analyzer.api.dto.request.SensorRequest;
import org.demo.co2analyzer.api.dto.response.Co2MetricsDTO;
import org.demo.co2analyzer.api.dto.response.MeasurementResponse;
import org.demo.co2analyzer.api.dto.response.SensorResponse;
import org.demo.co2analyzer.entity.Measurement;
import org.demo.co2analyzer.entity.Sensor;
import org.demo.co2analyzer.entity.SensorStatus;
import org.demo.co2analyzer.exception.Error;
import org.demo.co2analyzer.exception.NotFoundException;
import org.demo.co2analyzer.repository.MeasurementRepository;
import org.demo.co2analyzer.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorService {
    private static final int CO2_THRESHOLD = 2000;
    private final Predicate<Measurement> isAboveThreshold = m -> m.getReading() >= CO2_THRESHOLD;
    private final Predicate<Measurement> isBelowThreshold = m -> m.getReading() < CO2_THRESHOLD;

    private final SensorRepository sensorRepository;
    private final MeasurementRepository measurementRepository;
    private final AlertHistoryService alertHistoryService;

    public SensorResponse create(SensorRequest request) {
        log.info("Creating new sensor: {}", request);
        Sensor sensor = Sensor.builder()
                .name(request.name())
                .location(request.location())
                .build();
        sensorRepository.save(sensor);
        return sensor.toSensorResponse();
    }

    public SensorResponse getSensor(UUID id) {
        return getSensorById(id).toSensorResponse();
    }

    public Co2MetricsDTO getMetrics(UUID id) {
        Sensor sensor = getSensorById(id);
        return measurementRepository.findCo2MetricsFrom(sensor.getId(), OffsetDateTime.now().minusDays(30));
    }

    @Transactional
    public MeasurementResponse addMeasurement(UUID sensorId, MeasurementRequest request) {
        Sensor sensor = getSensorById(sensorId);
        Measurement measurement = saveMeasurement(sensor, request);
        SensorStatus oldStatus = sensor.getStatus();
        SensorStatus newStatus = getStatus(sensor, measurement);
        if (oldStatus != newStatus) {
            addOrEndAlert(oldStatus, newStatus, sensor);
            sensor.setStatus(newStatus);
            log.info("Status changed from {} to {} for sensor with id - {}", oldStatus, newStatus, sensor.getId());
            sensorRepository.save(sensor);
        }
        return measurement.toMeasurementResponse();
    }

    private Measurement saveMeasurement(Sensor sensor, MeasurementRequest request) {
        log.info("Adding measurement: {} for Sensor with id - {}", request, sensor.getId());
        Measurement measurement = Measurement.builder()
                .time(request.time())
                .sensor(sensor)
                .reading(request.reading())
                .build();
        log.info("Measurement saved wth id - {} for  sensor with id - {}", measurement.getId(), sensor.getId());
        return measurementRepository.save(measurement);
    }

    private void addOrEndAlert(SensorStatus oldStatus, SensorStatus newStatus, Sensor sensor) {
        if (oldStatus == SensorStatus.ALERT && newStatus == SensorStatus.OK) {
            alertHistoryService.endCurrentAlert(sensor);
        } else if (oldStatus == SensorStatus.WARN && newStatus == SensorStatus.ALERT) {
            alertHistoryService.addAlert(sensor);
        }
    }

    private SensorStatus getStatus(Sensor sensor, Measurement currentMeasurement) {
        var recent3Measurements = measurementRepository.findTop3BySensorIdOrderByTimeDesc(sensor.getId());
        if (recent3Measurements.size() >= 3) {
            if (sensor.getStatus() == SensorStatus.ALERT) {
                return recent3Measurements.stream().allMatch(isBelowThreshold) ? SensorStatus.OK : SensorStatus.ALERT;
            }
            if (recent3Measurements.stream().allMatch(isAboveThreshold)) {
                return SensorStatus.ALERT;
            }
        }
        return isAboveThreshold.test(currentMeasurement) ? SensorStatus.WARN : SensorStatus.OK;
    }

    private Sensor getSensorById(UUID id) {
        return sensorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Error.SENSOR_NOT_FOUND));
    }
}
