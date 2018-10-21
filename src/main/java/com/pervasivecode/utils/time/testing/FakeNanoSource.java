package com.pervasivecode.utils.time.testing;

import com.pervasivecode.utils.time.api.CurrentNanosSource;

public class FakeNanoSource implements CurrentNanosSource {
  private long fakeNanos = 12345L;

  /**
   * Increment the internal counter by this many fake nanoseconds. Subsequent calls to
   * {@link #currentTimeNanoPrecision()} will return a value that is at least this much large than
   * the return value from prior calls.
   * 
   * @param additionalNanos The number of fake nanoseconds to advance the internal counter.
   */
  public void incrementTimeNanos(long additionalNanos) {
    fakeNanos += additionalNanos;
  }

  @Override
  public long currentTimeNanoPrecision() {
    long current = fakeNanos;
    incrementTimeNanos(1L);
    return current;
  }
}
