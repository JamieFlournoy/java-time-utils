package com.pervasivecode.utils.time;

import static com.pervasivecode.utils.time.RemainderHandling.ROUND;
import static java.util.Objects.requireNonNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * Formatter for a Duration.
 */
public class DurationFormatter {
  private static final BigInteger NANOS_PER_SECOND = BigInteger.valueOf(1_000_000_000L);

  private final DurationFormat format;
  private final Joiner partJoiner;

  public DurationFormatter(DurationFormat format) {
    this.format = requireNonNull(format);
    this.partJoiner = Joiner.on(format.partDelimiter());
  }

  private static BigInteger wholeDurationAsNanos(Duration duration) {
    BigInteger secondsPart = BigInteger.valueOf(duration.getSeconds());
    BigInteger nanosPart = BigInteger.valueOf(duration.getNano());
    return secondsPart.multiply(NANOS_PER_SECOND).add(nanosPart);
  }

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

      if (currentUnit == format.smallestUnit()
          && (format.numFractionalDigits() > 0 || format.remainderHandling() == ROUND)) {

        BigDecimal partValueWithFraction = new BigDecimal(bigNanosRemainingBeforeIntDivision)
            .divide(new BigDecimal(currentUnitInNanos));

        nf.setRoundingMode(RoundingMode.HALF_EVEN);
        nf.setMaximumFractionDigits(format.numFractionalDigits());
        String fractionPart = nf.format(partValueWithFraction);
        if (fractionPart.equals("0")) {
          return formatZero();
        }
        sb.append(fractionPart);
      } else {
        String wholeNumberPart = nf.format(partValue);
        sb.append(wholeNumberPart);
      }

      sb.append(format.unitSuffixes().get(currentUnit));
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

  private String formatZero() {
    NumberFormat nf = (NumberFormat) format.numberFormat().clone();
    nf.setMinimumFractionDigits(0);
    nf.setMaximumFractionDigits(0);
    return nf.format(0) + format.unitSuffixes().get(format.unitForZeroDuration());
  }
}
