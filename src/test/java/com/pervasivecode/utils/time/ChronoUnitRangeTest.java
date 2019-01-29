package com.pervasivecode.utils.time;

import static com.google.common.truth.Truth.assertThat;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.Test;

public class ChronoUnitRangeTest {
  @Test
  public void indexOf_shouldWork() {
    ChronoUnitRange range = new ChronoUnitRange();
    assertThat(range.indexOf(MINUTES)).isEqualTo(4);
    assertThat(range.indexOf(DAYS)).isEqualTo(7);
  }

  @Test
  public void range_withSameToAndFromValue_shouldReturnOneElement() {
    ChronoUnitRange range = new ChronoUnitRange();
    List<ChronoUnit> subRange = range.range(DAYS, DAYS);
    assertThat(subRange).containsExactly(DAYS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void range_withToLargerThanFrom_shouldReturnAscendingRange() {
    ChronoUnitRange range = new ChronoUnitRange();
    List<ChronoUnit> subRange = range.range(DAYS, YEARS);
    assertThat(subRange).containsExactly(DAYS, WEEKS, MONTHS, YEARS);
  }

  @Test(expected = IllegalArgumentException.class)
  public void range_withToValueLessThanFrom_shouldReturnDescendingRange() {
    ChronoUnitRange range = new ChronoUnitRange();
    List<ChronoUnit> subRange = range.range(YEARS, DAYS);
    assertThat(subRange).containsExactly(YEARS, MONTHS, WEEKS, DAYS);
  }
}
