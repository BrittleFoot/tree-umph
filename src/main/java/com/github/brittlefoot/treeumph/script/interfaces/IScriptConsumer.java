package com.github.brittlefoot.treeumph.script.interfaces;

import com.github.brittlefoot.treeumph.script.IScriptInterface;

import java.util.function.Consumer;


public interface IScriptConsumer<T> extends IScriptInterface, Consumer<T> {}
