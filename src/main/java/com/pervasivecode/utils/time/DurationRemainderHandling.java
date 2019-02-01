package com.pervasivecode.utils.time;

import java.math.RoundingMode;
import java.time.Duration;

/**
 * Strategies for formatting {@link Duration}s that have a remainder smaller than the smallest unit
 * of time that will be shown. That is, for a {@link Duration} of 1 hour and 59 minutes formatted in
 * weeks, days, and hours, what should be done with the remainder value of 59 minutes?
 */
public enum DurationRemainderHandling {
  /**
   * Drop the remainder value. This is equivalent to {@link RoundingMode#DOWN}.
   */
  TRUNCATE,

  /**
   * Round the remainder value to the nearest whole number using the rules described for
   * {@link RoundingMode#HALF_EVEN}.
   */
  ROUND_HALF_EVEN
}
