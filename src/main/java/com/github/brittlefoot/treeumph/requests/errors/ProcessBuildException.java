package com.github.brittlefoot.treeumph.requests.errors;


import com.github.brittlefoot.treeumph.process.Process;
import com.github.brittlefoot.treeumph.requests.TreeStage;

public class ProcessBuildException extends ProcessException {

    public ProcessBuildException(Process<TreeStage> built) {
        super(built);
    }

}
