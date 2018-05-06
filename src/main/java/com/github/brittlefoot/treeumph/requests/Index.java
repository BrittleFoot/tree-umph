package com.github.brittlefoot.treeumph.requests;


import com.github.brittlefoot.treeumph.requests.errors.IndexError;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Index<T> {

    private final IdGenerator idGenerator = new IdGenerator();
    private final Map<IdGenerator.Id, T> map = new ConcurrentHashMap<>();

    public IdGenerator.Id register(T t) {
        if (t == null) {
            throw new NullPointerException("Registration of null element is not allowed");
        }

        IdGenerator.Id id = idGenerator.getNewId();
        map.put(id, t);
        return id;
    }

    public T get(IdGenerator.Id id) {
        T t = map.get(id);
        if (t == null)
            throw new IndexError(id);

        return t;
    }

    public Map<IdGenerator.Id, T> getMap() {
        return map;
    }
}
