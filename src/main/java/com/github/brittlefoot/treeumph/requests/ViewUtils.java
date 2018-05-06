package com.github.brittlefoot.treeumph.requests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brittlefoot.treeumph.process.Process;

import java.io.IOException;
import java.util.Map;


public class ViewUtils {

    private final static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <T extends TreeStage> ScriptedProcessView toView(Process<T> process) throws IOException {
        return objectMapper.readValue(objectMapper.writeValueAsString(process), ScriptedProcessView.class);
    }

    /**
     * @deprecated Use {@link ProcessBuilder} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <TIn, TOut> TreeStage<TIn, TOut> buildStage(ScriptedTreeStageView thiz, TreeStage<?, TIn> parent) {

        ProcessFunction function = buildFunction(thiz.getFunction());
        TreeStage<TIn, TOut> treeStage = new TreeStage<>(thiz.getName(), parent, function);


        for (ScriptedTreeStageView optionalStage : thiz.getOptionalStages()) {
            treeStage.getOptionalStages().add(buildStage(optionalStage, treeStage));
        }

        for (ScriptedTreeStageView requiredStage : thiz.getRequiredStages()) {
            treeStage.getRequiredStages().add(buildStage(requiredStage, treeStage));
        }

        return treeStage;
    }

    /**
     * @deprecated Use {@link ProcessBuilder} instead
     */
    @Deprecated
    public static TreeStage buildStage(ScriptedTreeStageView thiz) {
        return buildStage(thiz, null);
    }

    /**
     * @deprecated Use {@link ProcessBuilder} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T, S> ProcessFunction<T, S> buildFunction(Map<String, Object> funcMap) {
        return objectMapper.convertValue(funcMap, ProcessFunction.class);
    }


    public static Process<TreeStage> buildProcess(ScriptedProcessView thiz) {
        Process<TreeStage> process = new Process<>(thiz.getName());

        for (ScriptedTreeStageView stage : thiz.getStages()) {
            process.addStage(buildStage(stage));
        }

        return process;
    }

}
