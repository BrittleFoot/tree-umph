package com.github.brittlefoot.treeumph.script.interfaces;


import com.github.brittlefoot.treeumph.script.IScriptInterface;

import java.util.function.Predicate;


public interface IScriptPredicate<T> extends IScriptInterface, Predicate<T> {}
