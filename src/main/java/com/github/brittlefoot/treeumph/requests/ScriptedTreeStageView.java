package com.github.brittlefoot.treeumph.requests;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class ScriptedTreeStageView implements Serializable {

    private ScriptedTreeStageView parent;
    private Map<String, Object> function;
    private List<ScriptedTreeStageView> optionalStages;
    private List<ScriptedTreeStageView> requiredStages;
    private Object result;
    private String name;
    private String status;

    public void setResult(Object result) {
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ScriptedTreeStageView getParent() {
        return parent;
    }

    public void setParent(ScriptedTreeStageView parent) {
        this.parent = parent;
    }

    public Map<String, Object> getFunction() {
        return function;
    }

    public void setFunction(Map<String, Object> function) {
        this.function = function;
    }

    public List<ScriptedTreeStageView> getOptionalStages() {
        return optionalStages;
    }

    public void setOptionalStages(List<ScriptedTreeStageView> optionalStages) {
        this.optionalStages = optionalStages;
    }

    public List<ScriptedTreeStageView> getRequiredStages() {
        return requiredStages;
    }

    public void setRequiredStages(List<ScriptedTreeStageView> requiredStages) {
        this.requiredStages = requiredStages;
    }

    public Object getResult() {
        return result;
    }

}
