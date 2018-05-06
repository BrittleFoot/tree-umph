package com.github.brittlefoot.treeumph.script;

import com.github.brittlefoot.treeumph.process.ProcessContext;
import com.github.brittlefoot.treeumph.script.bindings.IServiceBinding;
import com.github.brittlefoot.treeumph.script.errors.UnsatisfiedInterfaceException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.StringWriter;
import java.util.List;


public class Script<T extends IScriptInterface> {

    private static final String CONTEXT_NAME = "context";
    private final ScriptEngine engine;

    private final Text text;

    private final StringWriter stdout = new StringWriter();

    private final Class<T> desiredInterface;

    private boolean reevaluate = true;
    public Script(ScriptEngine engine, Text text, Class<T> desiredInterface) {
        this.engine = engine;
        this.text = text;
        this.desiredInterface = desiredInterface;
        engine.getContext().setWriter(stdout);
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public StringWriter getStdout() {
        return stdout;
    }

    public Text getText() {
        return text;
    }

    public T getInterface() {
        if (reevaluate) {
            try {
                engine.eval(text.getValue());
            } catch (ScriptException e) {
                // this text already has been successfully compiled
                //   so it shouldn't throw.
                throw new RuntimeException(e);
            }
        }

        // same as last comment. See {@link ScriptBuilder#eval}
        return ((Invocable) engine).getInterface(desiredInterface);
    }

    public void setContext(ProcessContext context) {
        engine.put(CONTEXT_NAME, context);
        this.reevaluate = true;
    }


    public static <T extends IScriptInterface> ScriptBuilder<T>.EngineSyntax ofInterface(Class<T> scriptInterface) {
        return new ScriptBuilder<T>().new InterfaceSyntax().withInterface(scriptInterface);
    }

    public static <T extends IScriptInterface> Script<T> build(
            Class<T> scriptInterface,
            ScriptEngine engine,
            Text text,
            List<IServiceBinding> bindings) throws ScriptException {

        ScriptBuilder<T> scriptBuilder = ofInterface(scriptInterface)
                .withEngine(engine)
                .withText(text);

        for (IServiceBinding binding : bindings) {
            scriptBuilder.addBinding(NamingStyle.lowerCamelCaseFrom(binding.getName()), binding);
        }

        return scriptBuilder.eval();

    }


    public static class ScriptBuilder<T extends IScriptInterface> {

        private Class<T> mScriptInterface;
        private Text mText;
        private ScriptEngine mEngine;

        private ScriptBuilder() {}

        class InterfaceSyntax {

            private InterfaceSyntax() {}

            EngineSyntax withInterface(Class<T> scriptInterface) {
                mScriptInterface = scriptInterface;
                return new EngineSyntax();
            }
        }


        public class EngineSyntax {

            private EngineSyntax() {}

            public TextSyntax withEngine(ScriptEngine engine) {
                mEngine = engine;
                return new TextSyntax();
            }
        }


        public class TextSyntax {

            private TextSyntax() {}

            public ScriptBuilder<T> withText(Text text) {
                mText = text;
                return ScriptBuilder.this;
            }

            public ScriptBuilder<T> withText(String text) {
                mText = new Text(text);
                return ScriptBuilder.this;
            }
        }

        public ScriptBuilder<T> addBinding(String name, Object value) {
            mEngine.put(name, value);
            return this;
        }

        public Script<T> eval() throws ScriptException {

            mEngine.eval(mText.getValue());

            T anInterface = ((Invocable) mEngine).getInterface(mScriptInterface);

            if (anInterface == null) {
                throw new UnsatisfiedInterfaceException(mScriptInterface.getCanonicalName());
            }

            return new Script<>(mEngine, mText, mScriptInterface);
        }

    }

}
