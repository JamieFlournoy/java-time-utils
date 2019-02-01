package com.pervasivecode.utils.time;

import static java.util.Objects.requireNonNull;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Map;


public class UnitSuffixProviders {
  private static final BigDecimal BIG_NEGATIVE_ONE = BigDecimal.ONE.negate();

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

  public static UnitSuffixProvider singularAndPlural(Map<ChronoUnit, String> singularUnitSuffixes,
      Map<ChronoUnit, String> pluralUnitSuffixes) {
    return singularAndPlural(singularUnitSuffixes, pluralUnitSuffixes, false);
  }

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
            || (negativeOneIsSingular &&
                (magnitude.compareTo(BIG_NEGATIVE_ONE) == 0));
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
