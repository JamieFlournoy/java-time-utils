package com.pervasivecode.utils.time.testing;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import com.pervasivecode.utils.time.CurrentNanosSource;

/**
 * This is a fake implementation of {@link CurrentNanosSource}, intended for use by test code.
 * <p>
 * This implementation is thread safe.
 */
public final class FakeNanoSource implements CurrentNanosSource {
  private AtomicLong fakeNanos = new AtomicLong(12345L);

  /**
   * Increment the internal counter by this many fake nanoseconds. Subsequent calls to
   * {@link #currentTimeNanoPrecision()} will return a value that is at least this much large than
   * the return value from prior calls.
   * 
   * @param additionalNanos The number of fake nanoseconds to advance the internal counter.
   */
  public void incrementTimeNanos(long additionalNanos) {
    fakeNanos.getAndAdd(additionalNanos);
  }

  /**
   * Return the stored fake "current" time value, and increment the stored value by 1.
   */
  @Override
  public long currentTimeNanoPrecision() {
    return fakeNanos.getAndIncrement();
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
    return Objects.equals(otherSource.fakeNanos, fakeNanos);
  }
}
