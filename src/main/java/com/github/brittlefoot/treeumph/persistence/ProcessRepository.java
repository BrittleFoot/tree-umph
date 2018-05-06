package com.github.brittlefoot.treeumph.persistence;


import com.github.brittlefoot.treeumph.requests.ScriptedProcessView;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface ProcessRepository extends MongoRepository<ScriptedProcessView, String> {

    ScriptedProcessView findByName(String name);

    List<ScriptedProcessView> findByNameLike(String name);

}
