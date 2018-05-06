package com.github.brittlefoot.treeumph.script.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.stream.Collectors;


public class BundledScriptEngine {

    private final static ScriptEngineManager manager = new ScriptEngineManager();

    public static ScriptEngine byName(String name) {
        return manager.getEngineByName(name);
    }

    public static List<String> getAvailableEngines() {
        return new ScriptEngineManager()
                .getEngineFactories()
                .stream()
                .map(ScriptEngineFactory::getLanguageName)
                .collect(Collectors.toList());
    }

}
