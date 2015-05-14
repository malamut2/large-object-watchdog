package com.github.malamut2.low;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WatchdogSampler
 * @author kgb
 */
public class WatchdogSampler {

    private static final Logger logger = Logger.getLogger(WatchdogSampler.class.getName());

    private final AtomicLong counter = new AtomicLong();

    public void sampleAllocation(int count, String desc, @SuppressWarnings ("UnusedParameters") Object newObj, long size) {
        AllocationEvent ev = new AllocationEvent(desc, size, count);
        cleanupStack(ev);
        logger.log(Level.INFO, "Event #" + counter.incrementAndGet(), ev);
    }

    private static final int removeFromStack = 2;

    private void cleanupStack(AllocationEvent ev) {
        StackTraceElement[] st = ev.getStackTrace();
        StackTraceElement[] newST = new StackTraceElement[st.length - removeFromStack];
        System.arraycopy(st, removeFromStack, newST, 0, newST.length);
        ev.setStackTrace(newST);
    }

    public long getNumberOfLargeAllocations() {
        return counter.get();
    }

}
