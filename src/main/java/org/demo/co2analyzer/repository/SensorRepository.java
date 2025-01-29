package org.demo.co2analyzer.repository;

import org.demo.co2analyzer.entity.Sensor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SensorRepository extends CrudRepository<Sensor, UUID> {
}
