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
public class TreeStage<TIn, TOut> extends Stage {

    private final TreeStage<?, TIn> parent;
    private final ProcessFunction<TIn, TOut> function;
    private final List<TreeStage<TOut, ?>> optionalStages;
    private final List<TreeStage<TOut, ?>> requiredStages;

    private TOut result = null;

    public TreeStage(TreeStage<?, TIn> parent, ProcessFunction<TIn, TOut> function) {
        this("", parent, function);
    }

    public TreeStage(String name, TreeStage<?, TIn> parent, ProcessFunction<TIn, TOut> function) {
        super(name);
        this.parent = parent;
        this.function = function;
        this.requiredStages = new ArrayList<>();
        this.optionalStages = new ArrayList<>();
    }

    public TreeStage<?, TIn> parent() {
        return parent;
    }

    public ProcessFunction<TIn, TOut> getFunction() {
        return function;
    }

    public TOut getResult() {
        return result;
    }

    public List<TreeStage<TOut, ?>> getRequiredStages() {
        return requiredStages;
    }

    public List<TreeStage<TOut, ?>> getOptionalStages() {
        return optionalStages;
    }

    /**
     * Small hook to get all features of {@link Stage#invoke(Runnable)} function and don't lose returned value
     *
     * @return function(context, arg) && handle stage completion status.
     */
    private TOut invokeWithArguments(ProcessContext context, TIn arg) {
        this.invoke(() -> this.apply(context, arg));
        return result;
    }

    /**
     * @return void, but it <em>must</em> set the result field;
     */
    public TOut apply(ProcessContext context, TIn argument) {
        CompletableFuture<TOut> rootFuture = CompletableFuture.supplyAsync(() -> function.apply(context, argument));

        for (TreeStage<TOut, ?> optionalStage : optionalStages) {
            rootFuture.thenApplyAsync(result -> optionalStage.invokeWithArguments(context, result));
        }

        CompletableFuture<?> waiter = CompletableFuture.completedFuture(null);
        for (TreeStage<TOut, ?> requiredStage : requiredStages) {

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


    public <TNext> TreeStage<TIn, TOut> addOption(String name, ProcessFunction<TOut, TNext> func) {
        this.optionalStages.add(new TreeStage<>(name, this, func));
        return this;
    }

    public <TNext> TreeStage<TIn, TOut> addRequirement(TreeStage<TOut, TNext> required) {
        this.requiredStages.add(required);
        return this;
    }

    public <TNext> TreeStage<TOut, TNext> also(String name, ProcessFunction<TOut, TNext> func) {
        TreeStage<TOut, TNext> optionalChild = new TreeStage<>(name, this, func);
        this.optionalStages.add(optionalChild);
        return optionalChild;
    }

    public <TNext> TreeStage<TOut, TNext> also(TreeStage<TOut, TNext> nextStage) {
        TreeStage<TOut, TNext> changed = nextStage.changeParent(this);
        this.optionalStages.add(changed);
        return changed;
    }

    public <TNext> TreeStage<TOut, TNext> let(String name, ProcessFunction<TOut, TNext> func) {
        TreeStage<TOut, TNext> requiredChild = new TreeStage<>(name, this, func);
        this.requiredStages.add(requiredChild);
        return requiredChild;
    }

    public <TNext> TreeStage<TOut, TNext> let(TreeStage<TOut, TNext> nextStage) {
        TreeStage<TOut, TNext> changed = nextStage.changeParent(this);
        this.requiredStages.add(changed);
        return changed;
    }

    /**
     * Returns copy of {@code stage} with parent changed to {@code newParent}.
     * Note that {@code newParent} won't be affected.
     *
     * @return <i>copy</i> of a {@code stage}
     */
    public TreeStage<TIn, TOut> changeParent(TreeStage<?, TIn> newParent) {
        return new TreeStage<>(getName(), newParent, getFunction());
    }

    /**
     * Creates {@link TreeStage} with no parent.
     */
    public static <T, S> TreeStage<T, S> of(String name, ProcessFunction<T, S> func) {
        return new TreeStage<>(name, null, func);
    }

}
