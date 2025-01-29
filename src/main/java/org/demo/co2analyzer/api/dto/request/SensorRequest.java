package org.demo.co2analyzer.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SensorRequest(@NotBlank String name, @NotBlank String location) {
}
