package org.demo.co2analyzer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends CodeDefinedException {
    public NotFoundException(Error error) {
        super(error.getMessage(), error.getKey());
    }
}
