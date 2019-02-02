package com.pervasivecode.utils.time;

import static com.google.common.base.Preconditions.checkState;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.Immutable;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;

/** This object holds configuration information for a {@link DurationFormatter} instance. */
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

  /**
   * The provider of unit suffixes that the {@link DurationFormatter} will use when formatting each
   * part of the {@link Duration}.
   * 
   * @return the {@link UnitSuffixProvider}.
   */
  public abstract UnitSuffixProvider unitSuffixProvider();

  /**
   * The delimiter to use to separate each part of the formatted value.
   *
   * @return the formatted-part delimiter.
   */
  public abstract String partDelimiter();

  /**
   * The NumberFormat which the DurationFormatter will use when formatting individual parts of
   * Duration values.
   * <p>
   * Note: the rounding mode and number of fraction digits properties of this NumberFormat instance
   * will be ignored.
   * <p>
   * Instead, the DurationFormatter will use a {@code minimumFractionDigits} of zero and a
   * {@code maximumFractionDigits} value of zero for all parts of the value other than the part
   * corresponding to the {@code smallestUnit}. For the value corresponding to the
   * {@code smallestUnit}, the {@code maximumFractionDigits} will be set to
   * {@link #numFractionalDigits()}, rounding mode will be set based on
   * {@link #remainderHandling()}, and {@code minimumFractionDigits} will be to zero.
   *
   * @return the NumberFormat.
   */
  public abstract NumberFormat numberFormat();

  /**
   * The largest allowable unit of time to use when formatting the {@link Duration} value.
   *
   * @return The largest allowable unit to use.
   */
  public abstract ChronoUnit largestUnit();

  /**
   * The smallest allowable unit of time to use when formatting the {@link Duration} value. Leading
   * and trailing parts of the formatted value with a quantity of zero will not be shown.
   *
   * @return The smallest allowable unit to use.
   */
  public abstract ChronoUnit smallestUnit();

  /**
   * The unit of time to use when formatting a {@link Duration} value of zero.
   *
   * @return The largest allowable unit to use.
   */
  public abstract ChronoUnit unitForZeroDuration();

  /**
   * Get the set of units that should not be included in the list of allowed units, even if they
   * fall within the range described by the smallestUnit and largestUnit properties.
   * 
   * @return The set of units which should not be used when formatting duration values.
   */
  public abstract Set<ChronoUnit> suppressedUnits();

  /**
   * Set the maximum number of digits to use to represent a fractional amount of the smallest
   * allowed unit. This value is used instead of the value of
   * {@link NumberFormat#getMaximumIntegerDigits()} in {@link DurationFormat#numberFormat()}.
   * <p>
   * Example: 36 hours formatted in units of days with a numFractionalDigits value of 2 will be
   * represented as 1.5 days.
   * 
   * @return The maximum number of fractional digits to use when formatting the value of the
   *         smallest allowed unit.
   */
  public abstract Integer numFractionalDigits();

  /**
   * Specify how values that are smaller than the smallest allowed unit with its fractional digits
   * are handled.
   * <p>
   * Example: A duration of 19 microseconds is formatted in units of hours, minutes, seconds, and
   * milliseconds, with a numFractionalDigits value of 2. 19 microseconds is equal to 0.019
   * milliseconds, but using only two digits to the right of the decimal, should that be formatted
   * as 0.01 or 0.02? {@link DurationRemainderHandling#TRUNCATE} will drop the 0.009 milliseconds;
   * {@link DurationRemainderHandling#ROUND_HALF_EVEN} will round the 0.009 milliseconds up to 0.01,
   * for a formatted value of 0.02 milliseconds.
   * 
   * @return The DurationRemainderHandling value that will control how rounding is performed.
   */
  public abstract DurationRemainderHandling remainderHandling();

  /**
   * Get a list of units which are greater than or equal to smallestUnit and less than or equal to
   * largestUnit, without the units specified by {@link #suppressedUnits()}. These units are the
   * ones that will be treated as available for formatting values. Units not in this list will not
   * be used by the {@link DurationFormatter}.
   * 
   * @return The list of available units for formatting.
   */
  @Memoized
  public List<ChronoUnit> units() {
    ImmutableList.Builder<ChronoUnit> unitsBuilder = ImmutableList.<ChronoUnit>builder();
    ImmutableSortedSet<ChronoUnit> rawRange = range(smallestUnit(), largestUnit());
    for (ChronoUnit unit : rawRange) {
      if (!suppressedUnits().contains(unit)) {
        unitsBuilder.add(unit);
      }
    }
    return unitsBuilder.build();
  }

  /**
   * Create an object that will build a {@link DurationFormatter} instance.
   * <p>
   * This builder is mostly unconfigured, except for the following default values:
   * <ul>
   * <li>suppressedUnits = { ChronoUnit.HALF_DAYS }</li>
   * <li>remainderHandling = DurationRemainderHandling.TRUNCATE</li>
   * </ul>
   *
   * @return a new {@link DurationFormat.Builder} instance.
   */
  public static Builder builder() {
    return new AutoValue_DurationFormat.Builder() //
        .setSuppressedUnits(ImmutableSet.of(ChronoUnit.HALF_DAYS))
        .setRemainderHandling(DurationRemainderHandling.TRUNCATE);
  }

  /**
   * Create an object that will build a {@link DurationFormatter} instance.
   * <p>
   * This new builder is configured with the values taken from the {@code format} parameter. In
   * effect, it is a mutable copy of that instance.
   *
   * @param format The format to use to configure the new Builder instance.
   * @return a new {@link DurationFormat.Builder} instance, with the same values as the instance
   *         passed via the {@code format} parameter.
   */
  public static Builder builder(DurationFormat format) {
    return builder() //
        .setUnitSuffixProvider(format.unitSuffixProvider()) //
        .setPartDelimiter(format.partDelimiter()) //
        .setNumberFormat(format.numberFormat()) //
        .setLargestUnit(format.largestUnit()) //
        .setSmallestUnit(format.smallestUnit()) //
        .setUnitForZeroDuration(format.unitForZeroDuration()) //
        .setNumFractionalDigits(format.numFractionalDigits()) //
        .setSuppressedUnits(format.suppressedUnits()) //
        .setRemainderHandling(format.remainderHandling());
  }

  /**
   * This object will build a {@link DurationFormat} instance. See {@link DurationFormat} for
   * explanations of what these values mean.
   */
  @AutoValue.Builder
  public static abstract class Builder {
    @Deprecated
    public Builder setUnitSuffixes(Map<ChronoUnit, String> unitSuffixes) {
      return setUnitSuffixProvider(UnitSuffixProviders.fixedSuffixPerUnit(unitSuffixes));
    }

    public abstract Builder setUnitSuffixProvider(UnitSuffixProvider suffixProvider);

    public abstract Builder setPartDelimiter(String partDelimiter);

    public abstract Builder setNumberFormat(NumberFormat numberFormat);

    public abstract Builder setLargestUnit(ChronoUnit largestUnit);

    public abstract Builder setSmallestUnit(ChronoUnit smallestUnit);

    public abstract Builder setUnitForZeroDuration(ChronoUnit unitForZeroDuration);

    public abstract Builder setSuppressedUnits(Set<ChronoUnit> suppressedUnits);

    public abstract Builder setNumFractionalDigits(Integer numFractionalDigits);

    public abstract Builder setRemainderHandling(DurationRemainderHandling remainderHandling);

    protected abstract DurationFormat buildInternal();

    /**
     * Create an instance of {@link DurationFormat} from this builder instance.
     * <p>
     * Field values are validated before the {@link DurationFormat} is returned.
     * 
     * @throws IllegalArgumentException if the smallestUnit is larger than the largestUnit.
     * @throws IllegalArgumentException if the UnitSuffixProvider cannot provide suffixes for all of
     *         the units specified by smallestUnit, largestUnit, and useHalfDays.
     * @throws IllegalArgumentException if the unitForZeroDuration is not one of the units specified
     *         by smallestUnit, largestUnit, and useHalfDays.
     * @throws IllegalArgumentException if numFractionalDigits is negative.
     * @return A valid {@link DurationFormat} instance.
     */
    public DurationFormat build() {
      DurationFormat format = buildInternal();

      requireSmallestNotLargerThanLargest(format);

      checkState(!format.units().isEmpty(),
          "suppressedUnits (%s) leaves no units available for formatting",
          format.suppressedUnits());

      requireLabelsForUsableUnits(format);

      checkState(format.units().contains(format.unitForZeroDuration()),
          "The unitForZeroDuration '%s' is not in the list of units: %s.",
          format.unitForZeroDuration(), format.units());

      checkState(format.numFractionalDigits() >= 0,
          "The number of fractional digits must be nonnegative. Got: %s",
          format.numFractionalDigits());

      return format;
    }

    private static void requireLabelsForUsableUnits(DurationFormat format) {
      for (int i = -2; i <= 2; i++) {
        for (ChronoUnit unit : format.units()) {
          String suffix = format.unitSuffixProvider().suffixFor(unit, i);
          checkState(suffix != null, "Missing unit suffix for quantity %s of unit %s", i, unit);
        }
      }
    }

    private static void requireSmallestNotLargerThanLargest(DurationFormat format) {
      ChronoUnit smallestUnit = format.smallestUnit();
      ChronoUnit largestUnit = format.largestUnit();
      checkState(smallestUnit.getDuration().compareTo(largestUnit.getDuration()) <= 0,
          "Invalid range of units: smallest is %s, largest is %s", smallestUnit.name(),
          largestUnit.name());
    }
  }
}
