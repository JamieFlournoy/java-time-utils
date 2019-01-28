package com.pervasivecode.utils.time;

import static com.google.common.base.Preconditions.checkArgument;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.util.Objects.requireNonNull;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import com.google.common.collect.ImmutableList;

/**
 * Formatter for a Duration. The format will consist of space-separated tokens, each of which
 * represents a time unit, such as 3 hours or 5 seconds, and each of which uses the time unit suffix
 * specified in the constructor. Leading parts with a zero value will be skipped; trailing parts
 * with a zero value will be shown.
 * <p>
 * The value will be represented with either milliseconds precision, if the value is smaller than 60
 * seconds and also has a fractional seconds component, or will be represented with seconds
 * precision. Values of less than one second are scaled to milliseconds; values larger than one
 * second are scaled to seconds and shown as a decimal value.
 * <p>
 *
 * Examples: With the suffixes provided by{@link DurationFormatter#US}: 12.345 seconds will be
 * represented as "12.345s". 0.345 seconds will be represented as "345ms". 3600 seconds will be
 * represented as "1h 0m 0s". One day minus one second (that is, 86399 seconds) will be represented
 * as "23h 59m 59s".
 */
public class DurationFormatter {
  private static final ImmutableList<ChronoUnit> TIME_SCALES =
      ImmutableList.of(WEEKS, DAYS, HOURS, MINUTES, SECONDS, MILLIS);
  private static final int INDEX_OF_MILLS_TIME_SCALE = TIME_SCALES.indexOf(MILLIS);
  private static final int INDEX_OF_SECONDS_TIME_SCALE = TIME_SCALES.indexOf(SECONDS); 
  private static final Duration ONE_MINUTE = Duration.ofMinutes(1);
  private static final Duration ONE_SECOND = Duration.ofSeconds(1);

  /**
   * Get an instance that will use abbreviated US suffixes such as "s" for seconds and "m" for
   * minutes.
   *
   * @return An instance that will format values in a form appropriate for the US locale.
   */
  public static DurationFormatter US() {
    return new DurationFormatter(ImmutableList.of("ms", "s", "m", "h", "d", "w"), " ", ".");
  }

  private final ImmutableList<String> suffixes;
  private final String wholeNumberDelimiter;
  private final String fractionDelimiter;

  /**
   * Create a formatter with the specified list of time unit suffixes.
   *
   * @param suffixes The time unit suffixes. This list must contain exactly six elements, in the
   *        following order: milliseconds, seconds, minutes, hours, days, and weeks.
   */
  public DurationFormatter(List<String> suffixes, String partDelimiter, String fractionDelimiter) {
    this.suffixes = ImmutableList
        .copyOf(requireNonNull(suffixes, "The list of suffixes is required.")).reverse();
    checkArgument(suffixes.size() == TIME_SCALES.size(),
        "The list of suffixes must have six elements.");
    this.wholeNumberDelimiter = requireNonNull(partDelimiter);
    this.fractionDelimiter = requireNonNull(fractionDelimiter);
  }

  /**
   * Format the specified Duration, using weeks, days, hours, minutes, seconds, and milliseconds as
   * needed.
   *
   * @param duration The Duration to format.
   * @return The formatted String representation of the Duration value.
   */
  public String format(Duration duration) {
    return format(duration, ChronoUnit.WEEKS, ChronoUnit.MILLIS);
  }

  /**
   * Format the specified Duration, using units up to and including the largestUnit, but nothing
   * larger.
   * <p>
   * Example: formatting a Duration.ofDays(1) with a largestUnit of ChronoUnit.SECONDS will result
   * in a formatted value of "1440s".
   *
   * @param duration The Duration to format.
   * @param The largest unit that should be used to format the value (if the value has a whole
   *        number component of that magnitude; otherwise it will be treated as a leading zero and
   *        skipped.)
   * @return The formatted String representation of the Duration value.
   */
  public String format(Duration duration, ChronoUnit largestUnit) {
    return format(duration, largestUnit, ChronoUnit.MILLIS);
  }

  private String formatZero(ChronoUnit smallestUnit) {
    int suffixIndex = Math.min(TIME_SCALES.indexOf(smallestUnit), INDEX_OF_SECONDS_TIME_SCALE);
    String suffix = suffixes.get(suffixIndex);
    return new StringBuilder().append('0').append(suffix).toString();
  }
  
  /**
   * @param duration The Duration to format.
   * @param The largest unit that should be used to format the value (if the value has a whole
   *        number component of that magnitude; otherwise it will be treated as a leading zero and
   *        skipped.)
   * @return The formatted String representation of the Duration value.
   */
  public String format(Duration duration, ChronoUnit largestUnit, ChronoUnit smallestUnit) {
    if (duration.isZero()) {
      return formatZero(smallestUnit);
    }
    // TODO reject smallestUnit > largestUnit

    boolean lessThanOneMinute = duration.compareTo(ONE_MINUTE) < 0;
    int startIndex = Math.max(TIME_SCALES.indexOf(largestUnit), 0);

    // Special cases for whether to show milliseconds:
    // - For duration > 1 minute, do not show millis.
    // - For 1 minute < duration < 1 second, show fractional seconds (including millis) with the
    //   seconds label.
    // - For duration < 1 second, show only millis with a millis label.
    // TODO add a config object, and provide boolean config fields to disable this functionality.
    int endIndex = Math.min(TIME_SCALES.indexOf(smallestUnit), INDEX_OF_MILLS_TIME_SCALE);
    final boolean showFractionalSeconds;
    if (startIndex < INDEX_OF_MILLS_TIME_SCALE && endIndex > INDEX_OF_SECONDS_TIME_SCALE) {
      if (lessThanOneMinute) {
        endIndex = INDEX_OF_MILLS_TIME_SCALE; // Show the millis value.
        boolean lessThanOneSecond = duration.compareTo(ONE_SECOND) < 0;
        showFractionalSeconds = !lessThanOneSecond; 
      } else { // The duration is longer than 1 minute.
        endIndex = INDEX_OF_SECONDS_TIME_SCALE; // Hide the millis value.
        showFractionalSeconds = false;
      }
    } else {
      showFractionalSeconds = false;
    }

    Duration remaining = duration;
    StringBuilder sb = new StringBuilder();
    boolean inLeadingZeroPart = true;
    boolean needLeadingDelimiter = false;
    for (int i = startIndex; i <= endIndex; i++) {
      ChronoUnit currentUnit = TIME_SCALES.get(i);
      long currentUnitMillis = currentUnit.getDuration().toMillis();
      long valOfCurrentUnit = remaining.toMillis() / currentUnitMillis;
      remaining = remaining.minus(Duration.ofMillis(valOfCurrentUnit * currentUnitMillis));

      if (inLeadingZeroPart) {
        if (valOfCurrentUnit == 0) {
          continue;
        } else {
          inLeadingZeroPart = false;
        }
      }

      if (needLeadingDelimiter) {
        sb.append((currentUnit == MILLIS && showFractionalSeconds) ? fractionDelimiter
            : wholeNumberDelimiter);
      }
      needLeadingDelimiter = true;

      sb.append(valOfCurrentUnit);

      boolean showCurrentSuffix = !(showFractionalSeconds && currentUnit == SECONDS);

      if (showCurrentSuffix) {
        int suffixIndex = i;
        if (currentUnit == MILLIS && showFractionalSeconds) {
          suffixIndex--;
        }
        sb.append(suffixes.get(suffixIndex));
      }
    }

    return sb.toString();
  }
}
