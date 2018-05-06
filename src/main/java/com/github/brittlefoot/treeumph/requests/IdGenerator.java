package com.github.brittlefoot.treeumph.requests;

import java.util.concurrent.atomic.AtomicInteger;


public class IdGenerator {


    private final AtomicInteger idPtr = new AtomicInteger();

    public Id getNewId() {
        Integer i = idPtr.incrementAndGet();
        return new Id(i.toString());
    }

    public static class Id {

        private final String id;

        private Id(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Id id1 = (Id) o;

            return id.equals(id1.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return id;
        }
    }


}
