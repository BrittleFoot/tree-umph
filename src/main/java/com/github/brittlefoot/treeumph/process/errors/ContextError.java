package com.github.brittlefoot.treeumph.process.errors;


public class ContextError extends Error {

    public ContextError(String message) {
        super(message);
    }

    public ContextError(String message, Throwable cause) {
        super(message, cause);
    }
}

