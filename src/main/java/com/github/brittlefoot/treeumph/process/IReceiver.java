package com.github.brittlefoot.treeumph.process;

@FunctionalInterface
public interface IReceiver<T> {

    T receive();

}
