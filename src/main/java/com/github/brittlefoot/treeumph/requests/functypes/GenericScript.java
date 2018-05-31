package com.github.brittlefoot.treeumph.requests.functypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.brittlefoot.treeumph.process.ProcessContext;
import com.github.brittlefoot.treeumph.script.IScriptInterface;
import com.github.brittlefoot.treeumph.script.Script;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptException;
import org.springframework.data.annotation.Transient;

public class GenericScript<I extends IScriptInterface> implements IScriptedProcess {

    private final Launcher<I> launcher;
    private final String language;

    @JsonIgnore
    @Transient
    protected final Script script;

    private String runtimeError = "";

    GenericScript(Script<I> script, Launcher<I> launcher, String language) throws ScriptException {
        this.script = script;
        this.launcher = launcher;
        this.language = language;
    }

    @Override
    public Object apply(ProcessContext context, Object object) {
        script.setContext(context);
        return launcher.launch(script.getInterface(), object);
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public String getText() {
        return script.getText().getValue();
    }

    @Override
    public List<String> getBindings() {
        return Collections.emptyList();
    }

    @Override
    public String getStdout() {
        return script.getStdout().toString();
    }

    @Override
    public String getStderr() {
        return runtimeError;
    }
}
