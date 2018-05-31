package com.github.brittlefoot.treeumph.requests;

import com.github.brittlefoot.treeumph.process.ProcessContext;
import com.github.brittlefoot.treeumph.process.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * This kind of stage allows users to create trees of stages
 * with all futures provided by {@link Process} class.
 * Stage completed when whole subtree of {@code requiredStages} will be fulfilled.
 * {@code optionalStages} branch acts like detached thread.
 * All results can be found through {@code result} field.
 */
public class TreeStageClean extends Stage {

    private final TreeStageClean parent;
    private final ProcessFunctionClean function;
    private final List<TreeStageClean> optionalStages;
    private final List<TreeStageClean> requiredStages;

    private Object result = null;

    public TreeStageClean(TreeStageClean parent, ProcessFunctionClean function) {
        this("", parent, function);
    }

    public TreeStageClean(String name, TreeStageClean parent, ProcessFunctionClean function) {
        super(name);
        this.parent = parent;
        this.function = function;
        this.requiredStages = new ArrayList<>();
        this.optionalStages = new ArrayList<>();
    }

    public TreeStageClean parent() {
        return parent;
    }

    public ProcessFunctionClean getFunction() {
        return function;
    }

    public Object getResult() {
        return result;
    }

    public List<TreeStageClean> getRequiredStages() {
        return requiredStages;
    }

    public List<TreeStageClean> getOptionalStages() {
        return optionalStages;
    }

    /**
     * Small hook to get all features of {@link Stage#invoke(Runnable)} function and don't lose returned value
     *
     * @return function(context, arg) && handle stage completion status.
     */
    private Object invokeWithArguments(ProcessContext context, Object arg) {
        this.invoke(() -> this.apply(context, arg));
        return result;
    }

    /**
     * @return void, but it <em>must</em> set the result field;
     */
    public Object apply(ProcessContext context, Object argument) {
        CompletableFuture<Object> rootFuture = CompletableFuture.supplyAsync(() -> function.apply(context, argument));

        for (TreeStageClean optionalStage : optionalStages) {
            rootFuture.thenApplyAsync(result -> optionalStage.invokeWithArguments(context, result));
        }

        CompletableFuture<?> waiter = CompletableFuture.completedFuture(null);
        for (TreeStageClean requiredStage : requiredStages) {

            CompletableFuture<?> requiredFuture = rootFuture
                    .thenApplyAsync(result -> requiredStage.invokeWithArguments(context, result));

            waiter = waiter.thenCompose(o -> requiredFuture);
        }

        try {

            waiter.get();
            result = rootFuture.get();
            return result;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    @Override
    public void accept(ProcessContext context) {
        result = apply(context, null);
    }


    public TreeStageClean addOption(String name, ProcessFunctionClean func) {
        this.optionalStages.add(new TreeStageClean(name, this, func));
        return this;
    }

    public TreeStageClean addRequirement(TreeStageClean required) {
        this.requiredStages.add(required);
        return this;
    }

    public TreeStageClean also(String name, ProcessFunctionClean func) {
        TreeStageClean optionalChild = new TreeStageClean(name, this, func);
        this.optionalStages.add(optionalChild);
        return optionalChild;
    }

    public TreeStageClean also(TreeStageClean nextStage) {
        TreeStageClean changed = nextStage.changeParent(this);
        this.optionalStages.add(changed);
        return changed;
    }

    public TreeStageClean let(String name, ProcessFunctionClean func) {
        TreeStageClean requiredChild = new TreeStageClean(name, this, func);
        this.requiredStages.add(requiredChild);
        return requiredChild;
    }

    public TreeStageClean let(TreeStageClean nextStage) {
        TreeStageClean changed = nextStage.changeParent(this);
        this.requiredStages.add(changed);
        return changed;
    }

    /**
     * Returns copy of {@code stage} with parent changed to {@code newParent}.
     * Note that {@code newParent} won't be affected.
     *
     * @return <i>copy</i> of a {@code stage}
     */
    public TreeStageClean changeParent(TreeStageClean newParent) {
        return new TreeStageClean(getName(), newParent, getFunction());
    }

    /**
     * Creates {@link TreeStageClean} with no parent.
     */
    public static  TreeStageClean of(String name, ProcessFunctionClean func) {
        return new TreeStageClean(name, null, func);
    }

}
