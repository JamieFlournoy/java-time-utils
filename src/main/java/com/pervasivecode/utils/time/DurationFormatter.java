package com.pervasivecode.utils.time;

import static com.pervasivecode.utils.time.DurationRemainderHandling.ROUND_HALF_EVEN;
import static java.util.Objects.requireNonNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * Formatter for a {@link Duration} value. {@link Duration}s are split into avaialble
 * {@link ChronoUnit}s as specified in the {@link DurationFormat}, using unit {@link Duration}s
 * obtained from {@link ChronoUnit} values.
 * <p>
 * The format is specified by a {@link DurationFormat}, allowing formats such as:
 * <ul>
 * <li>1s 370ms</li>
 * <li>1.37s</li>
 * <li>2w 3d</li>
 * <li>4w1d4h22m</li>
 * <li>6 weeks, 4 days, 23 hours</li>
 * <li>-1m 30s</li>
 * </ul>
 * Leading and trailing parts of the formatted representation which have a zero value are not shown.
 * That is, a formatted value of 70 minutes with allowable units of seconds, minutes, hours, and
 * days will not be "0d 1h 10m 0s"; instead, it would be formatted as "1h 10m".
 * <p>
 * Zero {@link Duration}s are always formatted with the unit specified in
 * {@link DurationFormat#unitForZeroDuration() unitForZeroDuration}.
 * <p>
 * Unit suffix strings are obtained from a {@link UnitSuffixProvider}, which has access to the unit
 * and the actual value being rendered, for the sake of maximum flexibility.
 * {@link UnitSuffixProviders} has factory methods for more typical cases such as one suffix per
 * unit, or one singular and one plural suffix per unit.
 * <p>
 * Negative {@link Duration} values are split so that only the first part of the formatted value is
 * formatted as a negative number; the smaller parts are formatted as positive numbers. See the
 * example above: -90 seconds is formatted as -1 minutes, 30 seconds (not -1 minutes, -30 seconds).
 * <p>
 * Note: {@link Duration} values are defined in terms of seconds and nanoseconds. Since calendars'
 * day, month, and year lengths vary over time, this class uses the {@link ChronoUnit#MONTHS} and
 * {@link ChronoUnit#YEARS} unit values that are approximations of actual calendar months and years.
 */
public final class DurationFormatter {
  private static final BigInteger NANOS_PER_SECOND = BigInteger.valueOf(1_000_000_000L);
  private static final BigInteger INT_MAX_AS_BIG = BigInteger.valueOf(Integer.MAX_VALUE);
  private static final BigInteger INT_MIN_AS_BIG = BigInteger.valueOf(Integer.MIN_VALUE);

  private final DurationFormat format;
  private final transient Joiner partJoiner;

  /**
   * Create a DurationFormatter.
   *
   * @param format The formatting rules to use.
   */
  public DurationFormatter(DurationFormat format) {
    this.format = requireNonNull(format);
    this.partJoiner = Joiner.on(format.partDelimiter());
  }

  private static BigInteger wholeDurationAsNanos(Duration duration) {
    BigInteger secondsPart = BigInteger.valueOf(duration.getSeconds());
    BigInteger nanosPart = BigInteger.valueOf(duration.getNano());
    return secondsPart.multiply(NANOS_PER_SECOND).add(nanosPart);
  }

  /**
   * Format a Duration.
   *
   * @param duration The Duration to format.
   * @return The formatted representation of the Duration.
   */
  public String format(Duration duration) {
    if (duration.isZero()) {
      return formatZero();
    }

    NumberFormat nf = (NumberFormat) format.numberFormat().clone();
    nf.setRoundingMode(RoundingMode.UNNECESSARY);
    nf.setMinimumFractionDigits(0);
    nf.setMaximumFractionDigits(0);

    int unitIndex = 0;
    ImmutableList<ChronoUnit> unitsInDescOrder = ImmutableList.copyOf(format.units()).reverse();
    ArrayList<String> parts = new ArrayList<>();

    BigInteger bigNanosRemaining = wholeDurationAsNanos(duration);

    for (; unitIndex < unitsInDescOrder.size(); unitIndex++) {
      ChronoUnit currentUnit = unitsInDescOrder.get(unitIndex);
      BigInteger currentUnitInNanos = wholeDurationAsNanos(currentUnit.getDuration());

      BigInteger bigNanosRemainingBeforeIntDivision = bigNanosRemaining;
      BigInteger[] quotientAndRemainder = bigNanosRemaining.divideAndRemainder(currentUnitInNanos);
      BigInteger partValue = quotientAndRemainder[0];
      bigNanosRemaining = quotientAndRemainder[1];

      if (parts.isEmpty()) {
        if (partValue.equals(BigInteger.ZERO) && currentUnit != format.smallestUnit()) {
          // Skip leading parts whose value is zero.
          continue;
        } else {
          // If the duration is negative, only show the first part as a negative value.
          // Example: Duration.ofHours(-25) -> "-1d 1h" rather than "-1d -1h".
          bigNanosRemaining = bigNanosRemaining.abs();
        }
      }

      StringBuilder sb = new StringBuilder();

      final String suffix;
      if (currentUnit == format.smallestUnit()
          && (format.numFractionalDigits() > 0 || format.remainderHandling() == ROUND_HALF_EVEN)) {
        BigDecimal partValueWithFraction = new BigDecimal(bigNanosRemainingBeforeIntDivision)
            .divide(new BigDecimal(currentUnitInNanos));

        BigDecimal roundedPartValue =
            partValueWithFraction.setScale(format.numFractionalDigits(), RoundingMode.HALF_EVEN);

        nf.setRoundingMode(RoundingMode.HALF_EVEN);
        nf.setMaximumFractionDigits(format.numFractionalDigits());
        String fractionPart = nf.format(partValueWithFraction);
        if (fractionPart.equals("0")) {
          return formatZero();
        }
        sb.append(fractionPart);
        suffix = format.unitSuffixProvider().suffixFor(currentUnit, roundedPartValue);
      } else {
        String wholeNumberPart = nf.format(partValue);
        sb.append(wholeNumberPart);
        suffix = suffixFor(currentUnit, partValue);
      }

      Objects.requireNonNull(suffix);
      sb.append(suffix);

      parts.add(sb.toString());

      if (bigNanosRemaining.equals(BigInteger.ZERO)) {
        break;
      }
    }

    return partJoiner.join(parts);
  }

  private String suffixFor(ChronoUnit unit, BigInteger partValue) {
    if ((partValue.compareTo(INT_MAX_AS_BIG) > 0) || (partValue.compareTo(INT_MIN_AS_BIG) < 0)) {
      return format.unitSuffixProvider().suffixFor(unit, new BigDecimal(partValue));
    }
    return format.unitSuffixProvider().suffixFor(unit, partValue.intValue());
  }

  private String formatZero() {
    String zeroSuffix =
        format.unitSuffixProvider().suffixFor(format.unitForZeroDuration(), BigDecimal.ZERO);
    NumberFormat nf = (NumberFormat) format.numberFormat().clone();
    nf.setMinimumFractionDigits(0);
    nf.setMaximumFractionDigits(0);
    return nf.format(0) + zeroSuffix;
  }

  @Override
  public int hashCode() {
    return Objects.hash(format);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof DurationFormatter)) {
      return false;
    }
    DurationFormatter otherFormatter = (DurationFormatter) other;
    return Objects.equals(otherFormatter.format, format);
  }
}
