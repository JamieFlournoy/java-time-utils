package com.pervasivecode.utils.time;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/** A simple implementation of a {@link PeriodicRunner} using a {@link ScheduledExecutorService}. */
public class SimplePeriodicRunner implements PeriodicRunner {
  private final Duration progressUpdateInterval;
  private final ScheduledExecutorService executor;
  private ScheduledFuture<?> scheduled = null;
  private Runnable task;

  /**
   * Create a SimplePeriodicRunner.
   * 
   * @param executor The executor to use to run the task.
   * @param period The rate at which the task should be run. This is the amount of time between the
   *        start of one run of the task and the start of the next run of the task.
   */
  SimplePeriodicRunner(ScheduledExecutorService executor, Duration period) {
    this.executor = checkNotNull(executor);
    this.progressUpdateInterval = checkNotNull(period);
  }

  @Override
  public void setPeriodicTask(Runnable task) {
    checkState(scheduled == null,
        "A task is already scheduled in this runner. Call stop() before setting a different "
            + "scheduled task, or create a new SimplePeriodicRunner instance to handle an "
            + "additional scheduled task.");
    this.task = checkNotNull(task);
  }

  @Override
  public void start() {
    checkState(task != null, "No periodic task has been set.");
    checkState(scheduled == null, "The task has already been started.");
    long periodInMillis = this.progressUpdateInterval.toMillis();
    scheduled =
        executor.scheduleAtFixedRate(task, periodInMillis, periodInMillis, TimeUnit.MILLISECONDS);
  }

  /**
   * Stop the Runnable task from executing in the future. Note that this will not interrupt the
   * Runnable task if it is currently executing; it will finish the current execution normally.
   */
  @Override
  public void stop() {
    checkState(scheduled != null,
        "The periodic task has not been started yet, or has been stopped.");
    scheduled.cancel(false);
    scheduled = null;
  }
}
