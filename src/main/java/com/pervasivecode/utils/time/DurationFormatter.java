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
 * Formatter for a {@link Duration} value.
 * <p>
 * The format is specified by a {@link DurationFormat}, allowing formats such as:
 * <ul>
 * <li>1s 370ms</li>
 * <li>1.37s</li>
 * <li>2w 3d</li>
 * <li>4w1d4h22m</li>
 * </ul>
 */
public class DurationFormatter {
  private static final BigInteger NANOS_PER_SECOND = BigInteger.valueOf(1_000_000_000L);
  private static final BigInteger INT_MAX_AS_BIG = BigInteger.valueOf(Integer.MAX_VALUE);
  private static final BigInteger INT_MIN_AS_BIG = BigInteger.valueOf(Integer.MIN_VALUE);

  private final DurationFormat format;
  private final Joiner partJoiner;

  /**
   * Create a DurationFormatter.
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
    if (parts.isEmpty()) {
      return formatZero();
    }

    return partJoiner.join(parts);
  }

  private String suffixFor(ChronoUnit unit, BigInteger partValue) {
    if ((partValue.compareTo(INT_MAX_AS_BIG) > 1) ||
        (partValue.compareTo(INT_MIN_AS_BIG) < 1)) {
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
}
