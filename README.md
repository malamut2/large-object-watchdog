The Large Object Watchdog is a Java agent which traces creation of all objects whose net size
cross a given threshold. It has been forked from [java-allocation-instrumenter], which in turn
uses [java.lang.instrument] with [ASM].

Compared to [java-allocation-instrumenter], which offers a very general method to trace all sorts
of object allocations, the Large Object Wachdog solves a narrow, specific task, but does so
right out-of-the box, without adding anything to your actual code. Original code which seemed
unnecessary in this context has been removed. Most notably, no non-array allocations are reported
by the Large Object Watchdog because non-array objects will not really grow to any noteworthy size
in real life. If you need to track allocations for small objects, please stick with the original
[java-allocation-instrumenter].

## How to get it

The code should eventually become available from Maven Central as:

```xml
<dependency>
  <groupId>com.github.malamut2</groupId>
  <artifactId>large-object-watchdog</artifactId>
  <version>1.0.0</version>
</dependency>
```

While this is not yet the case, you'll need to clone the repository and build the jar using Maven
on your own.

## Basic usage

Add the jar using the `-javaagent` parameter to the java startup command line. Note that, if using
the `-jar` parameter, the `-javaagent` parameter should preceed it. As per default, all allocations
with an object size of 1M or more will be reported. To change that value, use the `limit` parameter,
like here: 

```
java -javaagent:/some/path/large-object-watchdog-1.0.0.jar=limit=20k -jar /some/path/yoursoftware.jar 
```

Enjoy! :)

[java.lang.instrument]: http://java.sun.com/javase/6/docs/api/java/lang/instrument/package-summary.html
[ASM]: http://asm.ow2.org/
[java-allocation-instrumenter]: https://github.com/google/allocation-instrumenter
