package com.github.brittlefoot.treeumph.requests;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.brittlefoot.treeumph.process.ProcessContext;
import com.github.brittlefoot.treeumph.requests.functypes.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = None.class),
        @Type(value = ScriptedByteFunction.class),
        @Type(value = ScriptedBytePredicate.class),
        @Type(value = ScriptedByteSupplier.class),
        @Type(value = ByteSupplier.class)
})
public interface ProcessFunction<T, S> extends BiFunction<ProcessContext, T, S> {

    static List<String> getAvailableFunctionTypes() {
        return Arrays
                .stream(ProcessFunction.class.getAnnotation(JsonSubTypes.class).value())
                .map(e -> e.value().getSimpleName())
                .collect(Collectors.toList());
    }

}
