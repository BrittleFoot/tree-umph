package com.github.brittlefoot.treeumph.servlet.errors;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Process with such name already exist.")
public class NameAlreadyBoundError extends Error {

    public NameAlreadyBoundError(String s) {
        super(s);
    }

}
