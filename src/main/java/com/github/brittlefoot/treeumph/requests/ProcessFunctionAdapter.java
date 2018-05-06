package com.github.brittlefoot.treeumph.requests;


import com.github.brittlefoot.treeumph.process.ProcessContext;

import java.util.LinkedHashMap;
import java.util.Map;


public class ProcessFunctionAdapter extends LinkedHashMap<String, Object> implements ProcessFunction<Object, Object> {

    public ProcessFunctionAdapter(Map<? extends String, ?> m) {
        super(m);
    }

    @Override
    public Object apply(ProcessContext strings, Object o) {
        return null;
    }

}
