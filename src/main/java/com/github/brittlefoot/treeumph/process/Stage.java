package com.github.brittlefoot.treeumph.process;

import com.github.brittlefoot.treeumph.process.errors.StageExecutionError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;


public abstract class Stage {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private volatile StageStatus status = StageStatus.PENDING;

    private final String name;

    private Stage() {
        this("");
    }

    protected Stage(String name) {
        if (name == null) {
            throw new NullPointerException("String name");
        }
        this.name = name;
    }

    protected final StageStatus invoke(Runnable stageAction) {
        if (status != StageStatus.PENDING) {
            log.warn("This stage ({}) is already executed", this);
            return getStatus();
        }

        try {
            status = StageStatus.IN_PROCESS;
            //////////////////////;
            stageAction.run();
            //////////////////////;
            status = StageStatus.COMPLETED;
        } catch (Exception | Error e) {
            status = StageStatus.ERROR;
            throw new StageExecutionError(e);
        } finally {
            if (status != StageStatus.COMPLETED)
                status = StageStatus.ERROR;
        }

        return getStatus();
    }

    public final void refresh() {
        status = StageStatus.PENDING;
    }

    public final void cancel() {
        status = StageStatus.CANCELLED;
    }

    public final StageStatus getStatus() {
        return status;
    }

    public final String getName() {
        return name.isEmpty() ? this.getClass().getSimpleName() : name;
    }

    @Override
    public String toString() {
        return getName();
    }


    public static Stage of(Consumer<ProcessContext> consumer) {
        return new Stage() {
            public void accept(ProcessContext context) {
                consumer.accept(context);
            }
        };
    }

    public static Stage of(String name, Consumer<ProcessContext> consumer) {
        return new Stage(name) {
            public void accept(ProcessContext context) {
                consumer.accept(context);
            }
        };
    }

    protected abstract void accept(ProcessContext processContext);
}
