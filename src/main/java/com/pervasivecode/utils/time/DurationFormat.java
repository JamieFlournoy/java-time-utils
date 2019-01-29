package com.pervasivecode.utils.time;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DurationFormat {
  public abstract Map<ChronoUnit, String> unitSuffixes();

  public abstract String partDelimiter();

  public abstract String fractionDelimiter();

  public abstract ChronoUnit largestUnit();

  public abstract ChronoUnit smallestUnit();

  public abstract Integer numFractionalDigits();

  public static Builder builder() {
    return new AutoValue_DurationFormat.Builder();
  }

  @AutoValue.Builder
  public static abstract class Builder {
    public abstract Builder setUnitSuffixes(Map<ChronoUnit, String> unitSuffixes);

    public abstract Builder setPartDelimiter(String partDelimiter);

    public abstract Builder setFractionDelimiter(String fractionDelimiter);

    public abstract Builder setLargestUnit(ChronoUnit largestUnit);

    public abstract Builder setSmallestUnit(ChronoUnit smallestUnit);

    public abstract Builder setNumFractionalDigits(Integer numFractionalDigits);

    protected abstract DurationFormat buildInternal();

    public DurationFormat build() {
      DurationFormat format = buildInternal();

      ChronoUnit smallestUnit = format.smallestUnit();
      ChronoUnit largestUnit = format.largestUnit();
      if (smallestUnit.getDuration().compareTo(largestUnit.getDuration()) > 0) {
        String message = String.format("Invalid range of units: smallest is %s, largest is %s",
            smallestUnit.name(), largestUnit.name());
        throw new IllegalArgumentException(message);
      }

      List<ChronoUnit> formattingUnits = new ChronoUnitRange().range(smallestUnit, largestUnit);
      Set<ChronoUnit> unitsWithLabels = format.unitSuffixes().keySet();
      ArrayList<ChronoUnit> unitsWithoutLabels = new ArrayList<>();
      unitsWithoutLabels.addAll(formattingUnits);
      unitsWithoutLabels.removeAll(unitsWithLabels);
      if (!unitsWithoutLabels.isEmpty()) {
        StringBuilder sb = new StringBuilder("Missing unit suffix");
        if (unitsWithoutLabels.size() > 1) {
          sb.append("es");
        }
        sb.append(" for ");
        sb.append(unitsWithoutLabels.get(0).name());
        for (int i = 1; i < unitsWithoutLabels.size(); i++) {
          sb.append(", ");
          sb.append(unitsWithoutLabels.get(1).name());
        }
        throw new IllegalArgumentException(sb.toString());
      }

      if (format.numFractionalDigits() < 0) {
        throw new IllegalArgumentException(
            "The number of fractional digits must be nonnegative. Got: "
                + format.numFractionalDigits());
      }
      return format;
    }

  }

  public static Builder builder(DurationFormat format) {
    return builder() //
        .setUnitSuffixes(format.unitSuffixes()) //
        .setPartDelimiter(format.partDelimiter()) //
        .setFractionDelimiter(format.fractionDelimiter()) //
        .setLargestUnit(format.largestUnit()) //
        .setSmallestUnit(format.smallestUnit()) //
        .setNumFractionalDigits(format.numFractionalDigits());
  }
}
