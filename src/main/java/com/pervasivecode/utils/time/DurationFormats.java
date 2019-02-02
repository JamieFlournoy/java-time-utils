package com.pervasivecode.utils.time;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import com.google.common.collect.ImmutableMap;

/** Factory methods for DurationFormat instances. */
public class DurationFormats {
  private DurationFormats() {}

  /**
   * DurationFormat for US locales, using units from nanoseconds up to years, without fractions.
   * <p>
   * Examples:
   * <ul>
   * <li>60 seconds -&gt; "1m"
   * <li>3601 seconds: -&gt; "1h 1s"
   * <li>8 days -&gt; "1w 1d"
   * <li>8 days -&gt; "1w 1d"
   * </ul>
   *
   * @return An instance that will format values in a form appropriate for the US locale.
   */
  public static DurationFormat getUsDefaultInstance() {
    return DurationFormat.builder() //
        .setUnitSuffixProvider(usShortSuffixProvider()) //
        .setPartDelimiter(" ") //
        .setNumberFormat(NumberFormat.getInstance(Locale.US)) //
        .setLargestUnit(YEARS) //
        .setSmallestUnit(NANOS) //
        .setUnitForZeroDuration(SECONDS) //
        .setNumFractionalDigits(0) //
        .build();
  }

  /**
   * Get a UnitSuffixProvider that provides abbreviated US suffixes such as "µs" for microseconds
   * and "m" for minutes. Suffixes are included for units from nanoseconds through years, not
   * including {@link ChronoUnit#HALF_DAYS}.
   *
   * @return The UnitSuffixProvider for short US suffixes.
   */
  public static UnitSuffixProvider usShortSuffixProvider() {
    ImmutableMap<ChronoUnit, String> unitSuffixes = ImmutableMap.<ChronoUnit, String>builder() //
        .put(NANOS, "ns") //
        .put(MICROS, "µs") //
        .put(MILLIS, "ms") //
        .put(SECONDS, "s") //
        .put(MINUTES, "m") //
        .put(HOURS, "h") //
        .put(DAYS, "d") //
        .put(WEEKS, "w") //
        .put(MONTHS, "mo") //
        .put(YEARS, "y") //
        .build();
    UnitSuffixProvider suffixProvider = UnitSuffixProviders.fixedSuffixPerUnit(unitSuffixes);
    return suffixProvider;
  }
}
