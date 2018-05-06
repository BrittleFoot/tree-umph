package com.github.brittlefoot.treeumph.process;


import com.github.brittlefoot.treeumph.process.errors.NameError;
import com.github.brittlefoot.treeumph.process.errors.TypeError;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;


public class ProcessContext implements Iterable<String> {

    private final Map<String, Object> context;

    public Object getObject(String key) {
        return context.get(key);
    }

    public ProcessContext(Map<String, Object> context) {
        if (context == null) {
            context = new ConcurrentHashMap<>();
        }
        this.context = new ConcurrentHashMap<>(context);
    }

    public ProcessContext() {
        this(new ConcurrentHashMap<>());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) throws NameError, TypeError {
        try {
            if (context.containsKey(key))
                return (T) context.get(key);
            throw new NameError(key);
        }
        catch (ClassCastException e) {
            throw new TypeError(key, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        try {
            if (context.containsKey(key))
                return (T) context.get(key);
            return defaultValue;
        }
        catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public void put(String key, Object value) {
        context.put(key, value);
    }

    public void clear() {
        context.clear();
    }

    public void remove(String key) {
        context.remove(key);
    }

    @Override
    public Iterator<String> iterator() {
        return context.keySet().iterator();
    }

    @Override
    public void forEach(Consumer<? super String> action) {
        context.keySet().forEach(action);
    }

    public Map<String, Object> asMap() {
        return context;
    }

}
