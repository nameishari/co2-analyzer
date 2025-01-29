package org.demo.co2analyzer.repository;

import org.demo.co2analyzer.entity.AlertHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertHistoryRepository extends CrudRepository<AlertHistory, UUID> {
    Optional<AlertHistory> findOneBySensorIdAndEndTimeIsNull(UUID sensorId);
    List<AlertHistory> findAllBySensorId(UUID sensorId);
}
