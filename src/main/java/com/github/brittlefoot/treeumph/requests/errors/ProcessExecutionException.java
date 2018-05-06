package com.github.brittlefoot.treeumph.requests.errors;


import com.github.brittlefoot.treeumph.process.Process;
import com.github.brittlefoot.treeumph.requests.TreeStage;

public class ProcessExecutionException extends ProcessException {

    public ProcessExecutionException(Process<TreeStage> built) {
        super(built);
    }
}
