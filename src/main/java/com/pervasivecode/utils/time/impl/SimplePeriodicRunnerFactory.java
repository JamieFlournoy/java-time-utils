package com.pervasivecode.utils.time.impl;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.VisibleForTesting;

/**
 * Instances of this class will run a single Runnable task repeatedly at a rate specified in terms
 * of an interval.
 * <p>
 * Limitations:
 * <p>
 * <ul>
 * <li>Each instance of SimplePeriodicRunnerFactory wraps a single execution thread, so long-running
 * tasks whose duration exceeds the scheduling interval can cause subsequent executions to be late
 * in starting.
 * <li>Starting multiple concurrent tasks via the same SimplePeriodicRunnerFactory (by calling
 * {@link #getRunnerForInterval} multiple times, producing multiple instances of
 * SimplePeriodicRunner that share the same thread) can cause similar interactions, where a single
 * task can block all other eligible tasks from running on time.
 * </ul>
 */

public class SimplePeriodicRunnerFactory {

  @VisibleForTesting
  static ScheduledThreadPoolExecutor createExecutorService() {
    ScheduledThreadPoolExecutor stpe =
        new ScheduledThreadPoolExecutor(1, new ThreadPoolExecutor.AbortPolicy());

    // Do not run any not-yet-started, still-scheduled tasks when the executor is
    // told to shut down.
    stpe.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

    // Keep idle threads around for one second before being terminated. This means that if the
    // runner is executed with an executionInterval of less than one second, the same thread
    // instance will be used every time; otherwise, the thread will be terminated and a new one will
    // be started for every execution of the Runnable task.
    stpe.setKeepAliveTime(1, TimeUnit.SECONDS);

    stpe.setRemoveOnCancelPolicy(true);
    return stpe;
  }

  private final ScheduledExecutorService executor;

  /**
   * Create an instance that will produce SimplePeriodicRunner instances that all share the same
   * scheduling thread. (To avoid delays caused by long-running tasks, isolate them by using more
   * than one instance of this factory class, so separate scheduling threads are used to run the
   * tasks.)
   */
  public SimplePeriodicRunnerFactory() {
    this.executor = createExecutorService();
  }

  @VisibleForTesting
  SimplePeriodicRunnerFactory(ScheduledExecutorService executor) {
    this.executor = executor;
  }

  /**
   * Get an instance of a PeriodicRunner that will allow a Runnable to be executed at the rate
   * specified via the executionInterval.
   * 
   * @param executionInterval The desired amount of time to delay execution of the provided Runnable
   *        task after the previous start of execution of that task.
   */
  public SimplePeriodicRunner getRunnerForInterval(Duration executionInterval) {
    return new SimplePeriodicRunner(executor, executionInterval);
  }

  /**
   * Wait up to a specified amount of time for a currently-running Runnable task to finish, if any,
   * and then shut down. No more scheduled tasks will be run.
   * 
   * @param timeout How long to wait for currently-running tasks to finish.
   * @param unit The units of the timeout parameter.
   * @throws InterruptedException If the calling thread was interrupted while waiting for a
   *         currently-running task to finish.
   */
  public void shutdownGracefully(long timeout, TimeUnit unit) throws InterruptedException {
    executor.shutdown();
    executor.awaitTermination(timeout, unit);
    if (!executor.isTerminated()) {
      shutdownNow();
    }
  }

  /**
   * Interrupt a running task (if any) and do not run any scheduled tasks from now on.
   */
  public void shutdownNow() {
    executor.shutdownNow();
  }
}
