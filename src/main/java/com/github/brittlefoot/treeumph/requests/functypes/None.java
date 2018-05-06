package com.github.brittlefoot.treeumph.requests.functypes;

import com.github.brittlefoot.treeumph.process.ProcessContext;
import com.github.brittlefoot.treeumph.requests.ProcessFunction;


public class None implements ProcessFunction<Object, Object> {

    @Override
    public Object apply(ProcessContext strings, Object t) {
        return t;
    }
}
