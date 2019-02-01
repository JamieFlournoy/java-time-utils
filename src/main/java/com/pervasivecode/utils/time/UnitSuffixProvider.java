package com.pervasivecode.utils.time;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

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
