package org.demo.co2analyzer.api.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MeasurementResponse(UUID id, OffsetDateTime time, UUID sensorId, Integer reading) {
}
