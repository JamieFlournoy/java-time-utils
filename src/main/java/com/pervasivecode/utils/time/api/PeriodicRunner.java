package com.pervasivecode.utils.time.api;

/**
 * Implementations of this interface can run a single Runnable task repeatedly at a steady rate.
 * Callers can optionally stop (and then optionally re-start) the Runnable task.
 * <p>
 * 
 * @see com.pervasivecode.utils.time.impl.SimplePeriodicRunnerFactory PeriodicRunnerFactory
 * 
 * @see com.pervasivecode.utils.time.testing.FakePeriodicRunner FakePeriodicRunner
 */
public interface PeriodicRunner {
  public void setPeriodicTask(Runnable task);

  public void start();

  public void stop();
}
