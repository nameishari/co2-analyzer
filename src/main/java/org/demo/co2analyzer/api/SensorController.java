package org.demo.co2analyzer.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.demo.co2analyzer.api.dto.request.MeasurementRequest;
import org.demo.co2analyzer.api.dto.request.SensorRequest;
import org.demo.co2analyzer.api.dto.response.Co2MetricsDTO;
import org.demo.co2analyzer.api.dto.response.MeasurementResponse;
import org.demo.co2analyzer.api.dto.response.SensorResponse;
import org.demo.co2analyzer.service.SensorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public SensorResponse createSensor(@RequestBody @Valid SensorRequest request) {
        return sensorService.create(request);
    }

    @GetMapping("/{sensorId}")
    public SensorResponse getSensor(@PathVariable UUID sensorId) {
        return sensorService.getSensor(sensorId);
    }


    @PostMapping("/{sensorId}/measurement")
    @ResponseStatus(value = HttpStatus.CREATED)
    public MeasurementResponse addMeasurement(@RequestBody @Valid MeasurementRequest request, @PathVariable UUID sensorId) {
        return sensorService.addMeasurement(sensorId, request);
    }

    @GetMapping("/{sensorId}/metrics")
    public Co2MetricsDTO getSensorMeasurementMetrics(@PathVariable UUID sensorId) {
        return sensorService.getMetrics(sensorId);
    }

}

