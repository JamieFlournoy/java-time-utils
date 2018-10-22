package com.pervasivecode.utils.time.testing;

import java.time.Duration;
import java.time.Instant;
import com.pervasivecode.utils.time.api.TimeSource;

/**
 * This is a fake implementation of {@link TimeSource},
 * intended for use by test code.
 */
public class FakeTimeSource implements TimeSource {
  private static final Instant START_INSTANT = Instant.ofEpochMilli(987_654_321L);
  private static final Duration TIME_TO_ADD_AFTER_EVERY_NOW_CALL = Duration.ofMillis(137L);

  private boolean autoAdvance;
  private Instant currentTime = START_INSTANT;

  /**
   * Create a {@link FakeTimeSource} instance that automatically advances the time value each time
   * {@link #now()} is called.
   */
  public FakeTimeSource() {
    this(true);
  }

  /**
   * Create a {@link FakeTimeSource} instance.
   * 
   * @param autoAdvance whether this instance should automatically advance the time value each time
   *        {@link #now()} is called.
   */
  public FakeTimeSource(boolean autoAdvance) {
    this.autoAdvance = autoAdvance;
  }

  /**
   * Return the fake value representing the "current" time.
   * <p>
   * If this instance was created with the option to automatically advance the current time, it will
   * do so; otherwise, the current time will not be changed by calling this method.
   */
  @Override
  public Instant now() {
    Instant returnValue = currentTime;
    if (autoAdvance) {
      advance();
    }
    return returnValue;
  }

  /**
   * Advance the fake value representing the "current" time by an unspecified, fixed amount.
   */
  public void advance() {
    advance(TIME_TO_ADD_AFTER_EVERY_NOW_CALL);
  }

  /**
   * Advance the fake value representing the "current" time by a fixed amount specified by the
   * caller.
   * 
   * @param timeToAdd The amount of time to advance the fake value representing the "current" time.
   */
  public void advance(Duration timeToAdd) {
    currentTime = currentTime.plus(timeToAdd);
  }

  /**
   * Return the amount of time between the internal initial fake time value when this instance was
   * created, and the internal current fake time value stored by this instance.
   * @return The difference between the fake start time and the fake current time.
   */
  public Duration elapsedSoFar() {
    return Duration.between(START_INSTANT, currentTime);
  }
}
