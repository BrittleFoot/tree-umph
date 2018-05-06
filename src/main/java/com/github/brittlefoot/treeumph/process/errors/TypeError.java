package com.github.brittlefoot.treeumph.process.errors;

public class TypeError extends ContextError {

    public TypeError(String name, ClassCastException e) {
        super(String.format("Value for '%s' has invalid type: %s", name, e.getMessage()), e);
    }

}
