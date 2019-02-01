package com.pervasivecode.utils.time;

import static com.google.common.base.Preconditions.checkArgument;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

// TODO javadoc
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

  //TODO javadoc
  public abstract UnitSuffixProvider unitSuffixProvider();

  //TODO javadoc
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

  //TODO javadoc
  public abstract ChronoUnit largestUnit();

  //TODO javadoc
  public abstract ChronoUnit smallestUnit();

  //TODO javadoc
  public abstract ChronoUnit unitForZeroDuration();

  //TODO javadoc
  public abstract boolean useHalfDays();

  //TODO javadoc
  public abstract Integer numFractionalDigits();

  //TODO javadoc
  public abstract DurationRemainderHandling remainderHandling();

  //TODO javadoc
  public static Builder builder() {
    return new AutoValue_DurationFormat.Builder() //
        .setUseHalfDays(false) //
        .setRemainderHandling(DurationRemainderHandling.TRUNCATE);
  }

  //TODO javadoc
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

  //TODO javadoc
  @AutoValue.Builder
  public static abstract class Builder {
    @Deprecated
    public Builder setUnitSuffixes(Map<ChronoUnit, String> unitSuffixes) {
      return setUnitSuffixProvider(UnitSuffixProviders.fixedSuffixPerUnit(unitSuffixes));
    }

    public abstract Builder setUnitSuffixProvider(UnitSuffixProvider suffixProvider);

    public abstract Builder setPartDelimiter(String partDelimiter);

    /**
     * Set the NumberFormat which the DurationFormatter should use when formatting individual parts
     * of Duration values.
     * <p>
     * Note: the rounding mode and number of fraction digits properties of this NumberFormat
     * instance will be ignored. The DurationFormatter will set these based on the values passed to
     * {@link #setNumFractionalDigits(Integer)} and
     * {@link #setRemainderHandling(DurationRemainderHandling)}.
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

    public abstract Builder setRemainderHandling(DurationRemainderHandling remainderHandling);

    protected abstract DurationFormat buildInternal();

    //TODO javadoc
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
      for (int i = -2; i <= 2; i++) {
        for (ChronoUnit unit : format.units()) {
          String suffix = format.unitSuffixProvider().suffixFor(unit, i);
          Preconditions.checkArgument(suffix != null,
              "Missing unit suffix for quantity %s of unit %s", i, unit);
        }
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

  //TODO javadoc
  public static Builder builder(DurationFormat format) {
    return builder() //
        .setUnitSuffixProvider(format.unitSuffixProvider()) //
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
