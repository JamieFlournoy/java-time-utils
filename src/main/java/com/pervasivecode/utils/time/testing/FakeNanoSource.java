package com.pervasivecode.utils.time.testing;

import java.util.Objects;
import com.pervasivecode.utils.time.CurrentNanosSource;

/**
 * This is a fake implementation of {@link CurrentNanosSource}, intended for use by test code.
 */
public final class FakeNanoSource implements CurrentNanosSource {
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

  /**
   * Return the stored fake "current" time value, and increment the stored value by 1.
   */
  @Override
  public long currentTimeNanoPrecision() {
    long current = fakeNanos;
    incrementTimeNanos(1L);
    return current;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fakeNanos);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof FakeNanoSource)) {
      return false;
    }
    FakeNanoSource otherSource = (FakeNanoSource) other;
    return otherSource.fakeNanos == fakeNanos;
  }
}
