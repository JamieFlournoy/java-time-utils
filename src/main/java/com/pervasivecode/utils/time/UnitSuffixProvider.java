package com.pervasivecode.utils.time;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * A provider of unit suffixes for formatting {@link Duration} values of varying units.
 * <p>
 * Example: Formatting a {@link Duration} of 243 minutes in units of hours and minutes requires a
 * unit suffix for 4 (hours) and a unit suffix for 3 (minutes). If the desired formatted duration
 * string is "4hrs 3mins" then the UnitSuffixProvider will provide the "hrs" and "mins" unit
 * suffixes.
 */
public interface UnitSuffixProvider {
  /**
   * Get the appropriate suffix to use for the specified unit and magnitude.
   * <p>
   * This method will only be called if the magnitude cannot be represented as an int. Otherwise,
   * the {@link #suffixFor(ChronoUnit, int)} method will be used.
   *
   * @param unit The unit of the value being formatted.
   * @param magnitude The value being formatted.
   * @return The String suffix to use for a time value with this unit and magnitude.
   */
  public String suffixFor(ChronoUnit unit, BigDecimal magnitude);

  /**
   * Get the appropriate suffix to use for the specified unit and magnitude.
   * <p>
   * This method will only be called if the magnitude can accurately be represented as an int.
   * Otherwise, the {@link #suffixFor(ChronoUnit, BigDecimal)} method will be used.
   *
   * @param unit The unit of the value being formatted.
   * @param magnitude The value being formatted.
   * @return The String suffix to use for a time value with this unit and magnitude.
   */
  public String suffixFor(ChronoUnit unit, int magnitude);
}
