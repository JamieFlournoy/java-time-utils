package com.pervasivecode.utils.time.api;

import java.time.Instant;

/**
 * Implementations of this interface supply values meant to represent the current wall-clock time.
 * <p>
 * Using this interface (or one like it) to introduce a level of indirection between your code and
 * the real world means that your tests can run as fast as possible, rather than having to include
 * real-world delays in the middle of the test.
 * <p>
 * When your code needs an implementation of TimeSource that uses real-world time, just use a lambda
 * expression of <code>()->Instant.now()</code>.
 * 
 * @see FakeTimeSource
 */
public interface TimeSource {
  public Instant now();
}
