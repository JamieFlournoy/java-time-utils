package com.pervasivecode.utils.time;

import static java.util.Objects.requireNonNull;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Factory methods for UnitSuffixProvider instances that behave in ways that are appropriate for
 * most locales.
 */
public class UnitSuffixProviders {
  private UnitSuffixProviders() {}

  private static final BigDecimal BIG_NEGATIVE_ONE = BigDecimal.ONE.negate();

  /**
   * Get a UnitSuffixProvider that provides unit suffixes that are fixed regardless of the magnitude
   * of the value being formatted.
   * <p>
   * Example: Durations in units of seconds formatted as 30s, 1s, and 0.5s all use the same "s"
   * suffix regardless of their magnitude.
   *
   * @param unitSuffixes A map of units of time to the suffix to use when formatting values in those
   *        units.
   * @return A UnitSuffixProvider that simply returns the appropriate value from unitSuffixes.
   *
   */
  public static UnitSuffixProvider fixedSuffixPerUnit(Map<ChronoUnit, String> unitSuffixes) {
    requireNonNull(unitSuffixes);
    return new UnitSuffixProvider() {

      @Override
      public String suffixFor(ChronoUnit unit, @SuppressWarnings("unused") BigDecimal magnitude) {
        return unitSuffixes.get(unit);
      }

      @Override
      public String suffixFor(ChronoUnit unit, @SuppressWarnings("unused") int magnitude) {
        return unitSuffixes.get(unit);
      }
    };
  }

  /**
   * Get a UnitSuffixProvider that provides unit suffixes that are either singluar or plural.
   * <p>
   * Singular is defined here as "equal to one". All other values are considered to be plural.
   * <p>
   * Example: Durations in units of seconds formatted as 30 seconds, 1 second, and 0.5 seconds use
   * either "second" or "seconds" suffixes. Negative one will be formatted as a plural value, so
   * with this example set of suffixes, it would be formatted as "-1 seconds".
   *
   * @param singularUnitSuffixes A map of units of time to the suffix to use when formatting
   *        singular values in those units.
   * @param pluralUnitSuffixes A map of units of time to the suffix to use when formatting plural
   *        values in those units.
   * @return A UnitSuffixProvider that returns the appropriate value from unitSuffixes.
   *
   */
  public static UnitSuffixProvider singularAndPlural(Map<ChronoUnit, String> singularUnitSuffixes,
      Map<ChronoUnit, String> pluralUnitSuffixes) {
    return singularAndPlural(singularUnitSuffixes, pluralUnitSuffixes, false);
  }

  /**
   * Get a UnitSuffixProvider that provides unit suffixes that are either singluar or plural, with a
   * configurable definition of whether negative one is considered to be singular.
   * <p>
   * Singular is defined here as "equal to one" or (if the {@code negativeOneIsSingular} parameter
   * is true) "equal to positive one or negative one". All other values are considered to be plural.
   * <p>
   * Example: Durations in units of seconds formatted as 30 seconds, 1 second, and 0.5 seconds use
   * either "second" or "seconds" suffixes. If the {@code negativeOneIsSingular} parameter is true,
   * negative one will be formatted as a singular value, so with this example set of suffixes, it
   * would be formatted as "-1 second".
   *
   * @param singularUnitSuffixes A map of units of time to the suffix to use when formatting
   *        singular values in those units.
   * @param pluralUnitSuffixes A map of units of time to the suffix to use when formatting plural
   *        values in those units.
   * @param negativeOneIsSingular If true, the value negative one (-1) will be treated as a singular
   *        value. Otherwise, it will be treated as plural (exactly as
   *        {@link #singularAndPlural(Map, Map)} does).
   * @return A UnitSuffixProvider that returns the appropriate value from unitSuffixes.
   *
   */
  public static UnitSuffixProvider singularAndPlural(Map<ChronoUnit, String> singularUnitSuffixes,
      Map<ChronoUnit, String> pluralUnitSuffixes, boolean negativeOneIsSingular) {
    requireNonNull(singularUnitSuffixes);
    requireNonNull(pluralUnitSuffixes);

    return new UnitSuffixProvider() {

      @Override
      public String suffixFor(ChronoUnit unit, BigDecimal magnitude) {
        // We use .compareTo here because BigDecimal#equals considers 1 and 1.000 not equal,
        // since the scale value differs (0 vs. 3). BigDecimal#compareTo is less strict.
        boolean singular = (magnitude.compareTo(BigDecimal.ONE) == 0)
            || (negativeOneIsSingular && (magnitude.compareTo(BIG_NEGATIVE_ONE) == 0));
        return suffixFor(unit, singular);
      }

      @Override
      public String suffixFor(ChronoUnit unit, int magnitude) {
        boolean singular = magnitude == 1 || (negativeOneIsSingular && magnitude == -1);
        return suffixFor(unit, singular);
      }

      private String suffixFor(ChronoUnit unit, boolean singular) {
        return (singular ? singularUnitSuffixes : pluralUnitSuffixes).get(unit);
      }
    };
  }
}
