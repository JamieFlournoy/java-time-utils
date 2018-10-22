package com.pervasivecode.utils.time.testing;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.pervasivecode.utils.time.api.PeriodicRunner;

/**
 * This is a fake implementation of {@link PeriodicRunner}, intended for use by test code.
 */
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


  /**
   * Set an internal flag saying that this runner is in the started state.
   * @throws IllegalStateException if no task has been provided.
   */
  @Override
  public void start() {
    checkState(task != null, "No periodic task has been set.");
    checkState(!this.started, "The task has already been started.");
    this.started = true;
  }

  /**
   * Set an internal flag saying that this runner is <i>not</i> in the started state.
   * @throws IllegalStateException if the task is already not in the started state. 
   */
  @Override
  public void stop() {
    checkState(started, "The periodic task is not running.");
    this.started = false;
  }

  /**
   * Simulate the periodic timer firing and running the {@link Runnable} task. This will invoke the
   * specified task once.
   * <p>
   * This will also verify that this runner is in the started state: that is, that the
   * {@link #start()} method has been called more recently than the last call to {@link #stop()}, if
   * any.
   * @throws IllegalStateException if this runner is not in the started state.
   */
  public void runOnce() {
    checkState(started, "The periodic task has not been started yet, or has been stopped.");
    this.task.run();
  }
}
