package com.pervasivecode.utils.time;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/** A periodic runner of a single {@link Runnable} task. */
public class SimplePeriodicRunner implements PeriodicRunner {
  private final Duration progressUpdateInterval;
  private final ScheduledExecutorService executor;
  private ScheduledFuture<?> scheduled = null;
  private Runnable task;

  SimplePeriodicRunner(ScheduledExecutorService executor, Duration progressUpdateInterval) {
    this.executor = checkNotNull(executor);
    this.progressUpdateInterval = checkNotNull(progressUpdateInterval);
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
