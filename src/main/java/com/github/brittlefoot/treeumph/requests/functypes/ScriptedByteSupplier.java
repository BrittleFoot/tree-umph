package com.github.brittlefoot.treeumph.requests.functypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.brittlefoot.treeumph.script.interfaces.IScriptByteSupplier;

import javax.script.ScriptException;
import java.util.List;


public class ScriptedByteSupplier extends ScriptedProcess<Void, byte[], IScriptByteSupplier> {

    public ScriptedByteSupplier(
            @JsonProperty(value = "language", required = true) String language,
            @JsonProperty(value = "text", required = true) String text,
            @JsonProperty(value = "bindings", defaultValue = "[]") List<String> bindings
    ) throws ScriptException {

        super(IScriptByteSupplier.class, (supplier, aVoid) -> supplier.get(), language, text, bindings);

    }

}
