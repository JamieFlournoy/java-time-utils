package com.pervasivecode.utils.time.testing;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.pervasivecode.utils.time.api.PeriodicRunner;

public class FakePeriodicRunner implements PeriodicRunner {
  private Runnable task = null;
  public boolean started = false;

  @Override
  public void setPeriodicTask(Runnable task) {
    checkState(this.task == null,
        "A task is already scheduled in this runner. Call stop() before setting a different "
            + "scheduled task, or create a new SimplePeriodicRunner instance to handle an "
            + "additional scheduled task.");
    checkState(!started);
    this.task = checkNotNull(task);
  }

  @Override
  public void start() {
    checkState(task != null, "No periodic task has been set.");
    this.started = true;
  }

  @Override
  public void stop() {
    checkState(started, "The periodic task is not running.");
    this.started = false;
  }

  public void runOnce() {
    checkState(started, "The periodic task has not been started yet, or has been stopped.");
    this.task.run();
  }
}
