package com.github.brittlefoot.treeumph.servlet.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such process")
public class NameNotFoundError extends RuntimeException {

    public NameNotFoundError(String message) {
        super(message);
    }
}
