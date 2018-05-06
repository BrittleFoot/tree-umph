package com.github.brittlefoot.treeumph.requests;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;


public class ScriptedProcessView implements Serializable {

    private List<ScriptedTreeStageView> stages;

    @Id
    private String name;

    public List<ScriptedTreeStageView> getStages() {
        return stages;
    }

    public void setStages(List<ScriptedTreeStageView> stages) {
        this.stages = stages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
