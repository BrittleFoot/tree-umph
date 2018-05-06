package com.github.brittlefoot.treeumph.requests.functypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.brittlefoot.treeumph.process.ProcessContext;
import com.github.brittlefoot.treeumph.requests.ProcessFunction;
import com.github.brittlefoot.treeumph.script.IScriptInterface;
import com.github.brittlefoot.treeumph.script.NamingStyle;
import com.github.brittlefoot.treeumph.script.Script;
import com.github.brittlefoot.treeumph.script.Text;
import com.github.brittlefoot.treeumph.script.bindings.BindingLocator;
import com.github.brittlefoot.treeumph.script.bindings.IServiceBinding;
import com.github.brittlefoot.treeumph.script.engine.BundledScriptEngine;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


/**
 * This class inheritors allowed to be loaded from json via {@link com.fasterxml.jackson.databind.ObjectMapper}
 */
@Component
public abstract class ScriptedProcess<T, S, I extends IScriptInterface> implements ProcessFunction<T, S>, Serializable {

    private final String language;
    private final String text;
    private final List<String> bindings;

    @JsonIgnore
    @Transient
    private final BiFunction<I, T, S> scriptUsage;

    @JsonIgnore
    @Transient
    protected final Script<I> script;

    private String runtimeError;

    ScriptedProcess(Class<I> scriptInterface,
                    BiFunction<I, T, S> scriptUsage,
                    String language,
                    String text,
                    List<String> sBindings)
            throws ScriptException {

        if (sBindings == null)
            sBindings = new ArrayList<>();

        this.scriptUsage = scriptUsage;
        this.language = language;
        this.text = text;
        this.bindings = sBindings;
        this.runtimeError = "";

        BindingLocator bindingLocator = BindingLocator.getInstance();
        List<IServiceBinding> serviceBindings = sBindings.stream()
                .map(bindingName -> bindingLocator.get(bindingName, NamingStyle.LOWER_CAMEL_CASE))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        this.script = Script.build(
                scriptInterface,
                BundledScriptEngine.byName(language),
                new Text(text),
                serviceBindings
        );
    }

    @Override
    public S apply(ProcessContext context, T t) {
        script.setContext(context);

        S result;

        try {
            result = scriptUsage.apply(script.getInterface(), t);
        } catch (Exception | Error e) {
            runtimeError = e.getMessage();
            if (e.getCause() != null) {
                runtimeError = e.getCause().getMessage();
            }
            throw e;
        }
        return result;
    }

    public String getLanguage() {
        return language;
    }

    public String getText() {
        return text;
    }

    public List<String> getBindings() {
        return bindings;
    }

    public String getStdout() {
        return script.getStdout().toString();
    }

    public String getStderr() {
        return runtimeError;
    }
}
