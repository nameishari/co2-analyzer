package org.demo.co2analyzer.repository;

import org.demo.co2analyzer.api.dto.response.Co2MetricsDTO;
import org.demo.co2analyzer.entity.Measurement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends CrudRepository<Measurement, UUID> {
    List<Measurement> findTop3BySensorIdOrderByTimeDesc(UUID sensorId);
    List<Measurement> findAllBySensorId(UUID sensorId);
    @Query("""
        SELECT new org.demo.co2analyzer.api.dto.response.Co2MetricsDTO(
            COALESCE(MAX(m.reading), 0),
            COALESCE(AVG(m.reading), 0)
        )
        FROM Measurement m
        WHERE m.sensor.id = :sensorId AND m.time >= :fromDate
    """)
    Co2MetricsDTO findCo2MetricsFrom(UUID sensorId, OffsetDateTime fromDate);
}
