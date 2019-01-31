package com.pervasivecode.utils.time;

import static com.google.common.base.Preconditions.checkArgument;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.Immutable;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

@AutoValue
@Immutable
public abstract class DurationFormat {
  private static final Comparator<ChronoUnit> DURATION_COMPARATOR =
      (unit, otherUnit) -> unit.compareTo(otherUnit);

  private static final ImmutableSortedSet<ChronoUnit> CHRONO_UNIT_VALUES =
      ImmutableSortedSet.copyOf(DURATION_COMPARATOR, Lists.newArrayList(ChronoUnit.values()));

  private static ImmutableSortedSet<ChronoUnit> range(ChronoUnit from, ChronoUnit to) {
    return CHRONO_UNIT_VALUES.headSet(to, true).tailSet(from, true);
  }

  public abstract Map<ChronoUnit, String> unitSuffixes();

  public abstract String partDelimiter();

  /**
   * The NumberFormat which the DurationFormatter will use when formatting individual parts of
   * Duration values.
   * <p>
   * Note: the rounding mode and number of fraction digits properties of this NumberFormat instance
   * will be ignored. The DurationFormatter will set these based on the values of
   * {@link #numFractionalDigits()} and {@link #remainderHandling()}.
   *
   * @return the NumberFormat.
   */
  public abstract NumberFormat numberFormat();

  public abstract ChronoUnit largestUnit();

  public abstract ChronoUnit smallestUnit();

  public abstract ChronoUnit unitForZeroDuration();

  public abstract boolean useHalfDays();

  public abstract Integer numFractionalDigits();

  public abstract RemainderHandling remainderHandling();

  public static Builder builder() {
    return new AutoValue_DurationFormat.Builder() //
        .setUseHalfDays(false) //
        .setRemainderHandling(RemainderHandling.TRUNCATE);
  }

  @Memoized
  public List<ChronoUnit> units() {
    ImmutableSortedSet<ChronoUnit> rawRange = range(smallestUnit(), largestUnit());
    if (useHalfDays() || !rawRange.contains(ChronoUnit.HALF_DAYS)) {
      return rawRange.asList();
    }
    return ImmutableList.<ChronoUnit>builder() //
        .addAll(rawRange.headSet(ChronoUnit.HALF_DAYS)) //
        .addAll(rawRange.tailSet(ChronoUnit.HALF_DAYS, false)) //
        .build();
  }

  @AutoValue.Builder
  public static abstract class Builder {
    public abstract Builder setUnitSuffixes(Map<ChronoUnit, String> unitSuffixes);

    public abstract Builder setPartDelimiter(String partDelimiter);

    /**
     * Set the NumberFormat which the DurationFormatter should use when formatting individual parts
     * of Duration values.
     * <p>
     * Note: the rounding mode and number of fraction digits properties of this NumberFormat
     * instance will be ignored. The DurationFormatter will set these based on the values passed to
     * {@link #setNumFractionalDigits(Integer)} and
     * {@link #setRemainderHandling(RemainderHandling)}.
     *
     * @param numberFormat The NumberFormat to use when formatting duration values.
     * @return The Builder instance that has been modified by this method.
     */
    public abstract Builder setNumberFormat(NumberFormat numberFormat);

    public abstract Builder setLargestUnit(ChronoUnit largestUnit);

    public abstract Builder setSmallestUnit(ChronoUnit smallestUnit);

    public abstract Builder setUnitForZeroDuration(ChronoUnit unitForZeroDuration);

    public abstract Builder setUseHalfDays(boolean useHalfDays);

    public abstract Builder setNumFractionalDigits(Integer numFractionalDigits);

    public abstract Builder setRemainderHandling(RemainderHandling remainderHandling);

    protected abstract DurationFormat buildInternal();

    public DurationFormat build() {
      DurationFormat format = buildInternal();
      requireSmallestNotLargerThanLargest(format);
      requireLabelsForUsableUnits(format);
      checkArgument(format.units().contains(format.unitForZeroDuration()),
          "The unitForZeroDuration '%s' is not in the list of units: %s.",
          format.unitForZeroDuration(), format.units());
      checkArgument(format.numFractionalDigits() >= 0,
          "The number of fractional digits must be nonnegative. Got: %s",
          format.numFractionalDigits());

      return format;
    }

    private static void requireLabelsForUsableUnits(DurationFormat format) {
      List<ChronoUnit> formattingUnits = format.units();
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
    }

    private static void requireSmallestNotLargerThanLargest(DurationFormat format) {
      ChronoUnit smallestUnit = format.smallestUnit();
      ChronoUnit largestUnit = format.largestUnit();
      if (smallestUnit.getDuration().compareTo(largestUnit.getDuration()) > 0) {
        throw new IllegalArgumentException(
            String.format("Invalid range of units: smallest is %s, largest is %s",
                smallestUnit.name(), largestUnit.name()));
      }
    }

  }

  public static Builder builder(DurationFormat format) {
    return builder() //
        .setUnitSuffixes(format.unitSuffixes()) //
        .setPartDelimiter(format.partDelimiter()) //
        .setNumberFormat(format.numberFormat()) //
        .setLargestUnit(format.largestUnit()) //
        .setSmallestUnit(format.smallestUnit()) //
        .setUnitForZeroDuration(format.unitForZeroDuration()) //
        .setNumFractionalDigits(format.numFractionalDigits()) //
        .setUseHalfDays(format.useHalfDays()) //
        .setRemainderHandling(format.remainderHandling());
  }
}
