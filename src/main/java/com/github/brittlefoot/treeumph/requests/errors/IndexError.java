package com.github.brittlefoot.treeumph.requests.errors;


import com.github.brittlefoot.treeumph.requests.IdGenerator;

public class IndexError extends Error {

    public IndexError(IdGenerator.Id id) {
        super("Id `" + id + "` does not exist.");
    }
}
