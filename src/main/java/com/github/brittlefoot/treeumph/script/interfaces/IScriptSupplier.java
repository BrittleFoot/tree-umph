package com.github.brittlefoot.treeumph.script.interfaces;

import com.github.brittlefoot.treeumph.script.IScriptInterface;

import java.util.function.Supplier;


public interface IScriptSupplier<T> extends IScriptInterface, Supplier<T> {}
