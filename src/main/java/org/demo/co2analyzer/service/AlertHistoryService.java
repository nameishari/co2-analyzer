package org.demo.co2analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.co2analyzer.entity.AlertHistory;
import org.demo.co2analyzer.entity.Sensor;
import org.demo.co2analyzer.repository.AlertHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertHistoryService {
    private final AlertHistoryRepository alertHistoryRepository;

    public void addAlert(Sensor sensor) {
        log.info("Adding a new alert to history for sensor - {}", sensor.getId());
        AlertHistory alertHistory = AlertHistory.builder()
                .sensor(sensor)
                .build();
        alertHistoryRepository.save(alertHistory);
    }

    public void endCurrentAlert(Sensor sensor) {
     alertHistoryRepository.findOneBySensorIdAndEndTimeIsNull(sensor.getId())
        .ifPresent(alertHistory -> {
            log.info("Ending alert - {} for sensor - {}", alertHistory.getId(), sensor.getId());
            alertHistory.setEndTime(Instant.now());
            alertHistoryRepository.save(alertHistory);
        });
    }
}
