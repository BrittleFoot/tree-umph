package com.github.brittlefoot.treeumph.requests.functypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.brittlefoot.treeumph.script.interfaces.IScriptBytePredicate;

import javax.script.ScriptException;
import java.util.List;


public class ScriptedBytePredicate extends ScriptedProcess<byte[], Boolean, IScriptBytePredicate> {

    public ScriptedBytePredicate(
            @JsonProperty(value = "language", required = true) String language,
            @JsonProperty(value = "text", required = true) String text,
            @JsonProperty(value = "bindings", defaultValue = "[]") List<String> bindings
    ) throws ScriptException {

        super(IScriptBytePredicate.class, IScriptBytePredicate::test, language, text, bindings);

    }

}
