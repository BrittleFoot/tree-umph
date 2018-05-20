package com.github.brittlefoot.treeumph.controller;


import com.github.brittlefoot.treeumph.requests.ProcessFunction;
import com.github.brittlefoot.treeumph.script.NamingStyle;
import com.github.brittlefoot.treeumph.script.bindings.BindingLocator;
import com.github.brittlefoot.treeumph.script.engine.BundledScriptEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private BindingLocator bindingLocator;

    @RequestMapping(value = "/languages")
    public List<String> getLanguages() {
        return BundledScriptEngine.getAvailableEngines();
    }

    @RequestMapping(value = "/bindings")
    public List<String> getBindings() {
        return bindingLocator.getAvailableBindings(NamingStyle.LOWER_CAMEL_CASE);
    }

    @RequestMapping(value = "/function_types")
    public List<String> getFunctionTypes() {
        return ProcessFunction.getAvailableFunctionTypes();
    }

}
