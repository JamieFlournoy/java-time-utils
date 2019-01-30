package com.pervasivecode.utils.time;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Objects.requireNonNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
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

    int unitIndex = 0;
    ImmutableList<ChronoUnit> unitsInDescOrder = ImmutableList.copyOf(format.units()).reverse();

    ArrayList<String> parts = new ArrayList<>();

    BigInteger bigNanosRemaining = wholeDurationAsNanos(duration);

    for (; unitIndex < unitsInDescOrder.size(); unitIndex++) {
      ChronoUnit currentUnit = unitsInDescOrder.get(unitIndex);
      BigInteger currentUnitInNanos = wholeDurationAsNanos(currentUnit.getDuration());

      BigInteger[] quotientAndRemainder = bigNanosRemaining.divideAndRemainder(currentUnitInNanos);
      BigInteger partValue = quotientAndRemainder[0];
      bigNanosRemaining = quotientAndRemainder[1];

      // Skip leading parts whose value is zero.
      if (partValue.equals(BigInteger.ZERO) && parts.isEmpty()) {
        continue;
      }

      StringBuilder sb = new StringBuilder();

      if (currentUnit == format.smallestUnit() && format.numFractionalDigits() > 0) {
        BigDecimal partValueWithFraction = new BigDecimal(partValue) //
            .add(new BigDecimal(bigNanosRemaining).divide(new BigDecimal(currentUnitInNanos)));
        // TODO get this from the DurationFormat
        partValueWithFraction
            .round(new MathContext(format.numFractionalDigits(), RoundingMode.HALF_EVEN));
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        String fractionPart = nf.format(partValueWithFraction);
        sb.append(fractionPart);
      } else {
        sb.append(partValue);
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
    ChronoUnit zeroUnit = SECONDS;
    if (!format.units().contains(zeroUnit)) {
      zeroUnit = format.smallestUnit();
    }
    return "0" + format.unitSuffixes().get(zeroUnit);
  }
}
