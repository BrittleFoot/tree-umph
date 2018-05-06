package com.github.brittlefoot.treeumph.requests;

import com.github.brittlefoot.treeumph.process.Process;

import java.util.Stack;


public class ProcessManipulator {

    public static ProcessManipulator index(Process<TreeStage> process) {
        Index<TreeStage> index = new Index<>();

        Stack<TreeStage> stages = new Stack<>();
        stages.addAll(process.getStages());

        while (!stages.empty()) {
            TreeStage<?, ?> stage = stages.pop();
            index.register(stage);

            stages.addAll(stage.getRequiredStages());
            stages.addAll(stage.getOptionalStages());
        }

        return new ProcessManipulator(process, index);
    }


    private final Process<TreeStage> process;
    private final Index<TreeStage> index;

    public ProcessManipulator(String name) {
        this(new Process<>(name), new Index<>());
    }

    private ProcessManipulator(Process<TreeStage> process, Index<TreeStage> index) {
        this.process = process;
        this.index = index;
    }

    public IdGenerator.Id newStage(TreeStage stage) {
        IdGenerator.Id id = index.register(stage);
        process.addStage(stage);
        return id;
    }

    public TreeStage get(IdGenerator.Id id) {
        return index.get(id);
    }

    /**
     * Wrapper for {@link TreeStage#also(TreeStage)}.
     * Copies given {@code optionalStage} and attaches it to {@code targetTreeStage}
     *
     * @throws ClassCastException when {@code optionalStage} type is inconsistent
     */
    @SuppressWarnings("unchecked")
    public IdGenerator.Id also(IdGenerator.Id targetTreeStageId, TreeStage optionalStage) {
        return index.register(index.get(targetTreeStageId).also(optionalStage));
    }

    /**
     * Wrapper for {@link TreeStage#let(TreeStage)}
     * Copies given {@code requiredStage} and attaches it to {@code targetTreeStage}
     *
     * @throws ClassCastException when tree {@code requiredStage} type is inconsistent
     */
    @SuppressWarnings("unchecked")
    public IdGenerator.Id let(IdGenerator.Id targetTreeStageId, TreeStage requiredStage) {
        return index.register(index.get(targetTreeStageId).let(requiredStage));
    }

    public Process<TreeStage> getProcess() {
        return process;
    }

    public Index<TreeStage> getIndex() {
        return index;
    }
}
