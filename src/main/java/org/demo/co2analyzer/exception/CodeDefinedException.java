package org.demo.co2analyzer.exception;

import lombok.Getter;

@Getter
public class CodeDefinedException extends RuntimeException {
    private final String errorKey;

    public CodeDefinedException(String message, String errorKey) {
        super(message);
        this.errorKey = errorKey;
    }
}
