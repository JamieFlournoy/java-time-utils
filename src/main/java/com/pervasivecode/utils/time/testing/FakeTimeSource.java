package com.pervasivecode.utils.time.testing;

import java.time.Duration;
import java.time.Instant;
import com.pervasivecode.utils.time.api.TimeSource;

public class FakeTimeSource implements TimeSource {
  private static final Instant START_INSTANT = Instant.ofEpochMilli(987_654_321L);
  private static final Duration TIME_TO_ADD_AFTER_EVERY_NOW_CALL = Duration.ofMillis(137L);

  private boolean autoAdvance;
  private Instant currentTime = START_INSTANT;

  public FakeTimeSource() {
    this(true);
  }

  public FakeTimeSource(boolean autoAdvance) {
    this.autoAdvance = autoAdvance;
  }

  @Override public Instant now() {
    Instant returnValue = currentTime;
    if (autoAdvance) {
      advance();
    }
    return returnValue;
  }

  public void advance() {
    advance(TIME_TO_ADD_AFTER_EVERY_NOW_CALL);
  }

  public void advance(Duration timeToAdd) {
    currentTime = currentTime.plus(timeToAdd);
  }

  public Duration elapsedSoFar() {
    return Duration.between(START_INSTANT, currentTime);
  }
}
