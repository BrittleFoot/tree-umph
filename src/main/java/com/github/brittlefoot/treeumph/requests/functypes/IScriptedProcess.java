package com.github.brittlefoot.treeumph.requests.functypes;

import com.github.brittlefoot.treeumph.requests.ProcessFunction;
import java.util.List;

public interface IScriptedProcess extends ProcessFunction<Object, Object> {

    String getLanguage();

    String getText();

    List<String> getBindings();

    String getStdout();

    String getStderr();
}
