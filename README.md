
## What is this?

The Large Object Watchdog is a Java agent which traces creation of all large objects.
'Large' means here that their system-dependent net size crosses a given threshold.
The Larget Object Watchdog has been forked from [java-allocation-instrumenter], which in turn
uses [java.lang.instrument] with [ASM].

Compared to [java-allocation-instrumenter], which offers a very general method to trace all sorts
of object allocations, the Large Object Wachdog solves a narrow, specific task, but does so
right out of the box, without the need to add anything to your actual code.

Original code which seemed unnecessary in this context has been removed. Most notably, 
no non-array allocations are reported by the Large Object Watchdog because single non-array
objects will not really grow to any noteworthy net size in real life. 
If you need to track allocations for small objects, please stick with the original 
[java-allocation-instrumenter].

## How do I get it?

You can get the compiled jar along with source code from the [Latest Release].

It should eventually become available from Maven Central like this:

```xml
<dependency>
  <groupId>com.github.malamut2</groupId>
  <artifactId>large-object-watchdog</artifactId>
  <version>1.0.2</version>
</dependency>
```

## How do I use it?

Add the jar using the `-javaagent` parameter to the java startup command line. Note that, if using
the `-jar` parameter, the `-javaagent` parameter should preceed it. As per default, all allocations
with an object size of 1M or more will be reported. To change that value, use the `limit` parameter,
like here: 

```
java -javaagent:/some/path/large-object-watchdog-1.0.0.jar=limit=20k -jar /some/path/yoursoftware.jar 
```

For each allocation of a large object, you will then get a log message like this:

```
INFORMATION: Event #1
com.github.malamut2.low.AllocationEvent: Allocated int[10000], total bytes 40016
	at com.github.malamut2.low.TestMain.main(TestMain.java:12)
```

Using the Large Object Watchdog will slow down startup time and especially class loading, but should
have only neglectable effects on performance otherwise.

Enjoy! :)

[java.lang.instrument]: http://java.sun.com/javase/7/docs/api/java/lang/instrument/package-summary.html
[ASM]: http://asm.ow2.org/
[java-allocation-instrumenter]: https://github.com/google/allocation-instrumenter
[Latest Release]: https://github.com/malamut2/large-object-watchdog/releases/latest