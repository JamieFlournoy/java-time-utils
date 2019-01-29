package com.pervasivecode.utils.time;

import static com.google.common.base.Preconditions.checkArgument;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.util.Objects.requireNonNull;
import java.math.BigInteger;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Formatter for a Duration. 
 */
public class DurationFormatter {

  // Old, obsolete javadoc contents:

//  The format will consist of space-separated tokens, each of which
//  * represents a time unit, such as 3 hours or 5 seconds, and each of which uses the time unit suffix
//  * specified in the constructor. Leading parts with a zero value will be skipped; trailing parts
//  * with a zero value will be shown.
//  * <p>
//  * The value will be represented with either milliseconds precision, if the value is smaller than 60
//  * seconds and also has a fractional seconds component, or will be represented with seconds
//  * precision. Values of less than one second are scaled to milliseconds; values larger than one
//  * second are scaled to seconds and shown as a decimal value.
//  * <p>
//  *
//  * Examples: With the suffixes provided by{@link DurationFormatter#US}: 12.345 seconds will be
//  * represented as "12.345s". 0.345 seconds will be represented as "345ms". 3600 seconds will be
//  * represented as "1h 0m 0s". One day minus one second (that is, 86399 seconds) will be represented
//  * as "23h 59m 59s".
  
  
  private final DurationFormat format;
  private final Joiner partJoiner;

  public DurationFormatter(DurationFormat format) {
    this.format = requireNonNull(format);
    this.partJoiner = Joiner.on(format.partDelimiter());
  }
  
  public String format(Duration duration) {
    if (duration.isZero()) {
      return formatZero();
    }

    int unitIndex = 0;
    ImmutableList<ChronoUnit> unitsInDescOrder = ImmutableList.copyOf(format.units()).reverse();

    ArrayList<String> parts = new ArrayList<>(); 

    long secondsRemaining = duration.getSeconds();
    long nanosRemaining = duration.getNano();
    for (; unitIndex < unitsInDescOrder.size(); unitIndex++) {
      ChronoUnit currentUnit = unitsInDescOrder.get(unitIndex);
      long currentUnitInSeconds = currentUnit.getDuration().getSeconds();

      final long partValue; 
      if (currentUnitInSeconds > 0) {
        partValue = secondsRemaining / currentUnitInSeconds;
        secondsRemaining -= partValue * currentUnitInSeconds;
      } else {
        nanosRemaining += 1_000_000_000L * secondsRemaining;
        long currentUnitInNanos = currentUnit.getDuration().getNano();
        partValue = nanosRemaining / currentUnitInNanos;
        nanosRemaining -= partValue * currentUnitInNanos;
      }

      // Skip leading parts whose value is zero.
      if (partValue == 0 && parts.isEmpty()) {
        continue;
      }

      // TODO handle fractions
      StringBuilder sb = new StringBuilder();
      sb.append(partValue);
      sb.append(format.unitSuffixes().get(currentUnit));
      parts.add(sb.toString());

      if (secondsRemaining == 0 && nanosRemaining == 0) {
        break;
      }
    }
    if (parts.isEmpty()) {
      return formatZero();
    }

    return partJoiner.join(parts);
  }

  private String formatZero() {
    ChronoUnit zeroUnit = SECONDS;
    if (!format.units().contains(zeroUnit)) {
      zeroUnit = format.smallestUnit();
    }
    return "0" + format.unitSuffixes().get(zeroUnit);
  }
}
