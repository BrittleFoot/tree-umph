package com.github.brittlefoot.treeumph.controller;

import com.github.brittlefoot.treeumph.process.Process;
import com.github.brittlefoot.treeumph.requests.ScriptedProcessView;
import com.github.brittlefoot.treeumph.requests.TreeStage;
import com.github.brittlefoot.treeumph.requests.ViewUtils;
import com.github.brittlefoot.treeumph.requests.errors.ProcessException;
import com.github.brittlefoot.treeumph.services.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/process")
public class ProcessController {

    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);

    @Autowired
    private ProcessService processService;

    @RequestMapping(value = "/stored")
    public List<String> getStoredProcessesNames() {
        return processService.getStoredProcesses().stream()
                .map(ScriptedProcessView::getName)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/active")
    public List<String> getActiveProcessesNames() {
        return new ArrayList<>(processService.getActiveProcesses().keySet());
    }

    @RequestMapping(value = "/run")
    public ScriptedProcessView runProcess(@RequestParam String name)
            throws Exception {

        Map<String, Process<TreeStage>> processes = processService.getActiveProcesses();

        // todo: use nonblocking responces.
        Process<TreeStage> process = null;
        if (processes.containsKey(name)) {
            process = processes.get(name);
        }

        try {
            if (process == null) {
                process = processService.activate(name);
            }

            process.runAsync().get();

            ScriptedProcessView processView = ViewUtils.toView(process);
            processService.store(processView);

            return processView;

        } catch (ProcessException e) {
            return ViewUtils.toView(e.getFilledWithErrors());
        }

    }


    @RequestMapping(value = "/get")
    public Process<TreeStage> getProcess(@RequestParam String name) {
        Map<String, Process<TreeStage>> processes = processService.getActiveProcesses();

        if (processes.containsKey(name)) {
            return processes.get(name);
        }

        return processService.activateQuiet(name);
    }

    @RequestMapping(value = "/reload")
    public Process<TreeStage> loadProcessDb(@RequestParam String name) {
        return processService.activateQuiet(name);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Process<TreeStage> loadProcess(@RequestBody ScriptedProcessView processView) {
        processService.store(processView);
        return processService.activateQuiet(processView.getName());
    }


}
