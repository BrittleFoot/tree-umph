package com.github.brittlefoot.treeumph.script.bindings.util;

public class ServiceResponse {

    private final boolean result;
    private final String errorDescription;

    public ServiceResponse(boolean result, String errorDescription) {
        if (errorDescription == null)
            throw new NullPointerException("errorDescription must not be null");

        this.result = result;
        this.errorDescription = errorDescription;
    }

    public boolean isCorrect() {
        return result;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
