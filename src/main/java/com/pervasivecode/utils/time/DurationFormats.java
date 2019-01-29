package com.pervasivecode.utils.time;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import java.time.temporal.ChronoUnit;
import com.google.common.collect.ImmutableMap;

public class DurationFormats {
  /**
   * Get an instance that will use abbreviated US suffixes such as "s" for seconds and "m" for
   * minutes.
   *
   * @return An instance that will format values in a form appropriate for the US locale.
   */
  public static DurationFormat getUsDefaultInstance() {
    ImmutableMap<ChronoUnit, String> unitSuffixes = ImmutableMap.<ChronoUnit, String>builder() //
        .put(MILLIS, "ms") //
        .put(SECONDS, "s") //
        .put(MINUTES, "m") //
        .put(HOURS, "h") //
        .put(DAYS, "d") //
        .put(WEEKS, "w") //
        .build();

    return DurationFormat.builder() //
        .setUnitSuffixes(unitSuffixes) //
        .setPartDelimiter(" ") //
        .setFractionDelimiter(".") //
        .setLargestUnit(WEEKS) //
        .setSmallestUnit(MILLIS) //
        .setNumFractionalDigits(0) //
        .build();
  }

}
