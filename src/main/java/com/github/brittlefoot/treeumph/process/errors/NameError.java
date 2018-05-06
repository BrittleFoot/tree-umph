package com.github.brittlefoot.treeumph.process.errors;

public class NameError extends ContextError {

    public NameError(String name) {
        super(String.format("Name '%s' is not defined in this context", name));
    }

}
