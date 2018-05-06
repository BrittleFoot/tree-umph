package com.github.brittlefoot.treeumph.servlet.services;

import com.github.brittlefoot.treeumph.persistence.ProcessRepository;
import com.github.brittlefoot.treeumph.process.Process;
import com.github.brittlefoot.treeumph.requests.ProcessBuilder;
import com.github.brittlefoot.treeumph.requests.ScriptedProcessView;
import com.github.brittlefoot.treeumph.requests.TreeStage;
import com.github.brittlefoot.treeumph.requests.errors.ProcessBuildException;
import com.github.brittlefoot.treeumph.servlet.errors.NameAlreadyBoundError;
import com.github.brittlefoot.treeumph.servlet.errors.NameNotFoundError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class ProcessService {

    private Map<String, Process<TreeStage>> activeProcesses = new ConcurrentHashMap<>();

    @Autowired
    private ProcessRepository processRepository;


    /**
     * {@link ProcessService#store(ScriptedProcessView, boolean)} with {@code throwOnOverride = false}
     */
    public ScriptedProcessView store(ScriptedProcessView processView) {
        return store(processView, false);
    }

    /**
     * @param processView     process to store
     * @param throwOnOverride set {@code true} if it need to handle name override scenario
     * @return stored object
     * @throws NameAlreadyBoundError if {@code throwOnOverride == true} and process with same name already exist
     */
    public ScriptedProcessView store(ScriptedProcessView processView, boolean throwOnOverride) {
        if (throwOnOverride) {
            if (null != processRepository.findByName(processView.getName())) {
                throw new NameAlreadyBoundError(processView.getName() + " already exist");
            }
        }

        return processRepository.save(processView);
    }

    /**
     * Creates {@link Process} from {@link ScriptedProcessView} acquired from {@link ProcessRepository} by given name.
     * <p>
     * todo: what shall we do when process with such name already active?
     * <p>
     * note: now we just overwrite existed one
     *
     * @return created {@link Process} instance, also store it in {@code activeProcesses} map.
     * @throws NameNotFoundError when process with given name does not found.
     */
    public Process<TreeStage> activate(String byName) throws ProcessBuildException {
        ScriptedProcessView processView = processRepository.findByName(byName);

        if (processView == null) {
            throw new NameNotFoundError(byName);
        }

        Process<TreeStage> process;
        process = new ProcessBuilder(processView).build();
        activeProcesses.put(process.getName(), process);
        return process;

    }

    public Process<TreeStage> activateQuiet(String byName) {
        try {
            return activate(byName);
        } catch (ProcessBuildException e) {
            activeProcesses.remove(byName);
            return e.getFilledWithErrors();
        }
    }


    public Map<String, Process<TreeStage>> getActiveProcesses() {
        return activeProcesses;
    }

    public List<ScriptedProcessView> getStoredProcesses() {
        return processRepository.findAll();
    }

}
