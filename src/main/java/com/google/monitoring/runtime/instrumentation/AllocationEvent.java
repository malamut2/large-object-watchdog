package com.google.monitoring.runtime.instrumentation;

/**
 * AllocationEvent
 * @author kgb
 */
public class AllocationEvent extends Throwable {
    public AllocationEvent(String className, long size, int numElements) {
        super("Allocated " + className + "[" + numElements + "], total bytes " + size);
    }
}
