package com.github.brittlefoot.treeumph.requests;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.brittlefoot.treeumph.process.Process;
import com.github.brittlefoot.treeumph.requests.errors.ProcessBuildException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class ProcessBuilder {

    private final static Logger log = LoggerFactory.getLogger(ProcessBuilder.class);

    private final static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    private final ScriptedProcessView view;

    private boolean buildErrorOccured;

    public ProcessBuilder(ScriptedProcessView serializationTarget) {
        this.view = serializationTarget;
    }

    public Process<TreeStage> build() throws ProcessBuildException {
        Process<TreeStage> built = buildProcess(view);

        if (buildErrorOccured)
            throw new ProcessBuildException(built);

        return built;
    }


    @SuppressWarnings("unchecked")
    private <TIn, TOut> TreeStage<TIn, TOut> buildStage(ScriptedTreeStageView thiz, TreeStage<?, TIn> parent) {

        ProcessFunction function;

        try {
            function = buildFunction(thiz.getFunction());
        } catch (IllegalArgumentException e) {
            ProcessFunctionAdapter adapter = new ProcessFunctionAdapter(thiz.getFunction());
            adapter.put("error", e.getCause().getCause().getMessage());
            function = adapter;

            log.warn(e.getMessage(), e.getCause());
            buildErrorOccured |= true;
        }

        TreeStage<TIn, TOut> treeStage = new TreeStage<>(thiz.getName(), parent, function);


        for (ScriptedTreeStageView optionalStage : thiz.getOptionalStages()) {
            treeStage.getOptionalStages().add(buildStage(optionalStage, treeStage));
        }

        for (ScriptedTreeStageView requiredStage : thiz.getRequiredStages()) {
            treeStage.getRequiredStages().add(buildStage(requiredStage, treeStage));
        }

        return treeStage;
    }

    private TreeStage buildStage(ScriptedTreeStageView thiz) {
        return buildStage(thiz, null);
    }

    @SuppressWarnings("unchecked")
    private <T, S> ProcessFunction<T, S> buildFunction(Map<String, Object> funcMap) {
        return objectMapper.convertValue(funcMap, ProcessFunction.class);
    }


    private Process<TreeStage> buildProcess(ScriptedProcessView thiz) {
        Process<TreeStage> process = new Process<>(thiz.getName());

        for (ScriptedTreeStageView stage : thiz.getStages()) {
            process.addStage(buildStage(stage));
        }

        return process;
    }

}
