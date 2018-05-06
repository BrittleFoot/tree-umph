package com.github.brittlefoot.treeumph.process;

@FunctionalInterface
public interface ISender<T> {

    void send(T sendable);

}
