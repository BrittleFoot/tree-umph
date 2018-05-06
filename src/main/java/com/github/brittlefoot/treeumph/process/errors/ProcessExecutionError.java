package com.github.brittlefoot.treeumph.process.errors;

public class ProcessExecutionError extends Error {

    public ProcessExecutionError(String message) {
        super(message);
    }

    public ProcessExecutionError(String message, Throwable cause) {
        super(message, cause);
    }
}
