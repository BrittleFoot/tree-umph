package com.github.brittlefoot.treeumph.process;

import com.github.brittlefoot.treeumph.process.errors.ProcessExecutionError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class Process<TStage extends Stage> {

    private final Logger log = LoggerFactory.getLogger(Process.class);

    private final ProcessContext context;
    private final String name;
    private final List<TStage> stages = new ArrayList<>();

    public Process(ProcessContext context, String name) {
        this.context = context;
        this.name = name;
    }

    public Process(String name) {
        this(new ProcessContext(), name);
    }

    public Process() {
        this(new ProcessContext(), "unnamed");
    }

    public void addStage(TStage stage) {
        stages.add(stage);
    }

    /**
     * Chains all given stages and asynchronously but consistently executes them.
     *
     * @return completable future instance that completes when process ends.
     */
    public CompletableFuture<Void> runAsync() {
        CompletableFuture<StageStatus> future = CompletableFuture.completedFuture(StageStatus.COMPLETED);

        for (TStage stage : stages) {
            future = future.thenApplyAsync(previousStage -> {
                if (previousStage == StageStatus.CANCELLED) {
                    stage.cancel();
                    return stage.getStatus();
                }

                //////////////////////////////////////////////////////////////;
                StageStatus result = stage.invoke(() -> stage.accept(context));
                //////////////////////////////////////////////////////////////;

                if (result == StageStatus.COMPLETED) {
                    return result;
                }

                String error = "Stage `%s` failed. Unexpected execution result: " + result;
                throw new ProcessExecutionError(String.format(error, stage.getName()));

            }).exceptionally(throwable -> {
                log.info("Cancel all following tasks. Reason: {}", stage, throwable);
                return StageStatus.CANCELLED;
            });
        }
        return future.thenRun(() -> {});
    }

    public String reprStates() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (TStage stage : new ArrayList<>(stages))
            sb.append(String.format("%s. %12s\t%s\n", ++i, stage.getName(), stage.getStatus()));

        return sb.toString();
    }

    public void refresh() {
        context.clear();
        for (TStage stage : new ArrayList<>(stages)) {
            stage.refresh();
        }
    }

    public List<TStage> getStages() {
        return new ArrayList<>(stages);
    }

    public String getName() {
        return name;
    }
}
