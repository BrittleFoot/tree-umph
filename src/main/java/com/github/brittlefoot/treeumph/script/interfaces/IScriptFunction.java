package com.github.brittlefoot.treeumph.script.interfaces;


import com.github.brittlefoot.treeumph.script.IScriptInterface;

import java.util.function.Function;


public interface IScriptFunction<T, S> extends IScriptInterface, Function<T, S> {}
