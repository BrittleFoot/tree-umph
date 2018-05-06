package com.github.brittlefoot.treeumph.requests.functypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.brittlefoot.treeumph.script.interfaces.IScriptByteFunction;

import javax.script.ScriptException;
import java.util.List;


public class ScriptedByteFunction extends ScriptedProcess<byte[], byte[], IScriptByteFunction> {

    public ScriptedByteFunction(
            @JsonProperty(value = "language", required = true) String language,
            @JsonProperty(value = "text", required = true) String text,
            @JsonProperty(value = "bindings", defaultValue = "[]") List<String> bindings
    ) throws ScriptException {

        super(IScriptByteFunction.class, IScriptByteFunction::apply, language, text, bindings);

    }

}
