package com.github.brittlefoot.treeumph.requests.functypes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.brittlefoot.treeumph.process.ProcessContext;
import com.github.brittlefoot.treeumph.requests.ProcessFunction;


public class ByteSupplier implements ProcessFunction<Void, byte[]> {

    private final byte[] value;

    public ByteSupplier(@JsonProperty(value = "value", required = true) byte[] value) {
        this.value = value;
    }

    @Override
    public byte[] apply(ProcessContext strings, Void aVoid) {
        return value;
    }

    public byte[] getValue() {
        return value;
    }

}
