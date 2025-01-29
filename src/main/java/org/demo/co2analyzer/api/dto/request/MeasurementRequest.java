package org.demo.co2analyzer.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record MeasurementRequest(@NotNull Integer reading, @NotNull OffsetDateTime time) {
}
