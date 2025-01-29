package org.demo.co2analyzer.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Error {
    SENSOR_NOT_FOUND("sensor_not_found", "Provided sensor not found");
    private final String key;
    private final String message;
}
