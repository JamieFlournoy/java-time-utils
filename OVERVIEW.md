## Interfaces

`CurrentNanosSource` provides the current time in units of nanoseconds since the Unix epoch.

`TimeSource` provides the current time in terms of a `java.time.Instant`.

`PeriodicRunner` invokes a specified `Runnable` task periodically, after the caller calls `start()` and until the caller calls `stop()`.


## Real Implementations

### SimplePeriodicRunnerFactory
This is a very simple wrapper around a `java.util.concurrent.ScheduledThreadPoolExecutor` instance. 

`SimplePeriodicRunnerFactory` produces instances of `SimplePeriodicRunner`, each of which manages a single Runnable, all of which are invoked periodically by a shared ScheduledThreadPoolExecutor instance belonging to the factory. See the Javadoc documentation of `SimplePeriodicRunnerFactory` for caveats of this minimalist design.

### TimeSource

This can trivially be implemented as a lambda in your calling code, so no library implementation is provided:

```
TimeSource t = ()->Instant.now();
```

### CurrentNanosSource:

This can trivially be implemented as a lambda in your calling code, so no library implementation is provided:

```
CurrentNanosSource c = ()->System.nanoTime();

```

## Fake Implementations

### FakeNanoSource

This starts with a fixed number of nanoseconds since the epoch, and automatically increments the returned nanoseconds value by 1 each time `currentTimeNanoPrecision` is called. There is also an `incrementTimeNanos` method that lets a test simulate a delay of a specified number of nanoseconds.

### FakePeriodicRunner

This lets the caller set a `Runnable` task, start and stop it, and checks to make sure start and stop are called under sane conditions (start requires that a task has been set; stop requires that a task has been started). There is a `runOnce` method that tests can call to simulate the periodic timer firing and running the Runnable task.

### FakeTimeSource

This starts with a fixed number of millis since the epoch, and automatically advances the returned fake time by default (so each call to `now` will return a different `Instant` representing a slightly later point in time). The caller can also simulate a delay of a specified `java.time.Duration`.
