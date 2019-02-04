package com.pervasivecode.utils.time;

/**
 * An object that can run a single Runnable task repeatedly at a steady rate. Callers can optionally
 * stop (and then optionally re-start) the Runnable task.
 * <p>
 * Callers can also optionally replace a stopped periodic task with a different task; this new task
 * will be run at the same rate as the previous task.
 */
public interface PeriodicRunner {
  /**
   * Specify the task that this PeriodicRunner should run periodically. The specified task will not
   * be run until after the {@link #start()} method has been called.
   *
   * @param task The task to run periodically.
   * @throws IllegalStateException if a task has already been provided and has been started, but not
   *         stopped yet. To avoid this, stop the other task before calling this method.
   */
  public void setPeriodicTask(Runnable task);

  /**
   * Start calling the specified Runnable task periodically.
   *
   * @throws IllegalStateException if no task has been provided, or if the task has already been
   *         started.
   */
  public void start();

  /**
   * Stop calling the specified Runnable task periodically.
   *
   * @throws IllegalStateException if no task has been provided, or has been provided but has not
   *         been started.
   */
  public void stop();
}
