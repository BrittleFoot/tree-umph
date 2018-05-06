package com.github.brittlefoot.treeumph.process;

import java.util.function.Consumer;


@FunctionalInterface
public interface IStage extends Consumer<ProcessContext> {

    @Override
    void accept(ProcessContext context);
}
