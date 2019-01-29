package com.pervasivecode.utils.time;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class ChronoUnitRange extends ReorderedEnumRange<ChronoUnit> {
  private static final Comparator<ChronoUnit> DURATION_COMPARATOR =
      (unit, otherUnit) -> unit.compareTo(otherUnit);

  public ChronoUnitRange() {
    super(ChronoUnit.values(), ChronoUnit.class, DURATION_COMPARATOR);
  }
}
