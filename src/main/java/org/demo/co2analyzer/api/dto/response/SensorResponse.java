package org.demo.co2analyzer.api.dto.response;

import org.demo.co2analyzer.entity.SensorStatus;

import java.util.UUID;

public record SensorResponse(UUID id, String name, String location, SensorStatus status) {
}
