/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.monitoring.runtime.instrumentation;

import java.lang.instrument.Instrumentation;
import java.util.logging.Level;

/**
 * The logic for recording allocations, called from bytecode rewritten by
 * {@link AllocationInstrumenter}.
 *
 * @author jeremymanson@google.com (Jeremy Manson)
 * @author fischman@google.com (Ami Fischman)
 */
public class AllocationRecorder {
    static {
        // Sun's JVMs in 1.5.0_06 and 1.6.0{,_01} have a bug where calling
        // Instrumentation.getObjectSize() during JVM shutdown triggers a
        // JVM-crashing assert in JPLISAgent.c, so we make sure to not call it after
        // shutdown.  There can still be a race here, depending on the extent of the
        // JVM bug, but this seems to be good enough.
        // instrumentation is volatile to make sure the threads reading it (in
        // recordAllocation()) see the updated value; we could do more
        // synchronization but it's not clear that it'd be worth it, given the
        // ambiguity of the bug we're working around in the first place.
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                setInstrumentation(null);
            }
        });
    }

    // See the comment above the addShutdownHook in the static block above
    // for why this is volatile.
    private static volatile Instrumentation instrumentation = null;

    static Instrumentation getInstrumentation() {
        return instrumentation;
    }

    static void setInstrumentation(Instrumentation inst) {
        instrumentation = inst;
    }

    private static long watchdogThreshold = 1024 * 1024;

    private static final Sampler sampler= new WatchdogSampler();

    // Used for reentrancy checks
    private static final ThreadLocal<Boolean> recordingAllocation = new ThreadLocal<Boolean>();

    public static void recordAllocation(Class<?> clazz, Object newObj) {
    }

    /**
     * Records the allocation.  This method is invoked on every allocation
     * performed by the system.
     *
     * @param count the count of how many instances are being
     *   allocated, if an array is being allocated.  If an array is not being
     *   allocated, then this value will be -1.
     * @param desc the descriptor of the class/primitive type
     *   being allocated.
     * @param newObj the new <code>Object</code> whose allocation is being
     *   recorded.
     */
    public static void recordAllocation(int count, String desc, Object newObj) {
        if (count < 0) {
            return;
        }
        if (recordingAllocation.get() == Boolean.TRUE) {
            return;
        } else {
            recordingAllocation.set(Boolean.TRUE);
        }

        if (instrumentation != null) {
            long objectSize = instrumentation.getObjectSize(newObj);
            if (objectSize > watchdogThreshold) {
                sampler.sampleAllocation(count, desc, newObj, objectSize);
            }
        }

        recordingAllocation.set(Boolean.FALSE);

    }

    public static void setWatchdogThreshold(String numberAsStringOrig) {
        try {
            String numberAsString = numberAsStringOrig.toLowerCase();
            char ch = numberAsString.charAt(numberAsString.length() - 1);
            long factor = 1;
            if (Character.isAlphabetic(ch)) {
                numberAsString = numberAsString.substring(0, numberAsString.length() - 1);
                switch (ch) {
                    case 'k':
                        factor = 1024;
                        break;
                    case 'm':
                        factor = 1024 * 1024;
                        break;
                    case 'g':
                        factor = 1024 * 1024 * 1024;
                        break;
                    default:
                        factor = 1;
                        break;
                }
            }
            long result = factor * Long.parseLong(numberAsString.trim());
            watchdogThreshold = result;
        } catch (Exception e) {
            AllocationInstrumenter.logger.log(Level.WARNING, "Illegal value of '" + numberAsStringOrig + "' for limit parameter", e);
        }
        // !kgb
    }
}
