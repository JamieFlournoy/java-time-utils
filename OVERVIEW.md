# Code Overview

If you prefer Javadocs, they are available on `javadoc.io`:

[![Javadocs](https://www.javadoc.io/badge/com.pervasivecode/time-utils.svg)](https://www.javadoc.io/doc/com.pervasivecode/time-utils)


## Interfaces

### [CurrentNanosSource](src/main/java/com/pervasivecode/utils/time/CurrentNanosSource.java)

An object that provides the current time since the Unix epoch, with nanosecond precision.

### [PeriodicRunner](src/main/java/com/pervasivecode/utils/time/PeriodicRunner.java)

An object that can run a single Runnable task repeatedly at a steady rate.

### [TimeSource](src/main/java/com/pervasivecode/utils/time/TimeSource.java)

An object that supplies values meant to represent the current wall-clock time.

### [UnitSuffixProvider](src/main/java/com/pervasivecode/utils/time/UnitSuffixProvider.java)

A provider of unit suffixes for formatting [Duration](https://docs.oracle.com/javase/10/docs/api/java/time/Duration.html?is-external=true) values of varying units.

## Enums

### [DurationRemainderHandling](src/main/java/com/pervasivecode/utils/time/DurationRemainderHandling.java)

Strategies for formatting Durations that have a remainder smaller than the smallest unit of time that will be shown.

## Real Implementations

### [DurationFormat](src/main/java/com/pervasivecode/utils/time/DurationFormat.java)

This object holds configuration information for a DurationFormatter instance.

### [DurationFormat.Builder](src/main/java/com/pervasivecode/utils/time/DurationFormat.java)

This object will build a DurationFormat instance.

### [DurationFormats](src/main/java/com/pervasivecode/utils/time/DurationFormats.java)

Factory methods for DurationFormat instances.

### [DurationFormatter](src/main/java/com/pervasivecode/utils/time/DurationFormatter.java)

Formatter for a Duration value.

### [SimplePeriodicRunner](src/main/java/com/pervasivecode/utils/time/SimplePeriodicRunner.java)

A simple implementation of a PeriodicRunner using a ScheduledExecutorService.

### [SimplePeriodicRunnerFactory](src/main/java/com/pervasivecode/utils/time/SimplePeriodicRunner.java)

Instances of this class will run a single Runnable task repeatedly at a rate specified in terms of an interval.

### [UnitSuffixProviders](src/main/java/com/pervasivecode/utils/time/UnitSuffixProviders.java)

Factory methods for UnitSuffixProvider instances that behave in ways that are appropriate for most locales.

### Intentionally omitted:

These interfaces can trivially be implemented as a lambda in your calling code, so no library implementation is provided:

#### TimeSource

```
TimeSource t = ()->Instant.now();
```

#### CurrentNanosSource:

```
CurrentNanosSource c = ()->System.nanoTime();

```

## Fake Implementations

### [FakeNanoSource](src/main/java/com/pervasivecode/utils/time/testing/FakeNanoSource.java)

This starts with a fixed number of nanoseconds since the epoch, and automatically increments the returned nanoseconds value by 1 each time `currentTimeNanoPrecision` is called. There is also an `incrementTimeNanos` method that lets a test simulate a delay of a specified number of nanoseconds.

### [FakePeriodicRunner](src/main/java/com/pervasivecode/utils/time/testing/FakePeriodicRunner.java)

This lets the caller set a `Runnable` task, start and stop it, and checks to make sure start and stop are called under sane conditions (start requires that a task has been set; stop requires that a task has been started). There is a `runOnce` method that tests can call to simulate the periodic timer firing and running the Runnable task.

### [FakeTimeSource](src/main/java/com/pervasivecode/utils/time/testing/FakeTimeSource.java)

This starts with a fixed number of millis since the epoch, and automatically advances the returned fake time by default (so each call to `now` will return a different `Instant` representing a slightly later point in time). The caller can also simulate a delay of a specified `java.time.Duration`.
