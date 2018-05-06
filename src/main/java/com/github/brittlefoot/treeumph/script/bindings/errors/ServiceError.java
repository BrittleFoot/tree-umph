package com.github.brittlefoot.treeumph.script.bindings.errors;

public class ServiceError extends Error {

    public ServiceError(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceError(String message) {
        super(message);
    }

}
