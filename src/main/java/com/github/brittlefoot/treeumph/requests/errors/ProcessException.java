package com.github.brittlefoot.treeumph.requests.errors;


import com.github.brittlefoot.treeumph.process.Process;
import com.github.brittlefoot.treeumph.requests.TreeStage;

public class ProcessException extends Exception {

    private Process<TreeStage> filledWithErrors;

    public ProcessException(Process<TreeStage> built) {
        this.filledWithErrors = built;
    }

    public Process<TreeStage> getFilledWithErrors() {
        return filledWithErrors;
    }
}
