package com.pervasivecode.utils.time;

import static com.google.common.truth.Truth.assertThat;
import static java.time.temporal.ChronoUnit.DAYS;
import java.math.BigDecimal;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;

public class UnitSuffixProvidersTest {
  @Test
  public void singularAndPluralProvider_withNegativeOneSingular_shouldWork() {
    UnitSuffixProvider dayOrDaysProvider = UnitSuffixProviders
        .singularAndPlural(ImmutableMap.of(DAYS, "day"), ImmutableMap.of(DAYS, "days"), true);

    assertThat(dayOrDaysProvider.suffixFor(DAYS, 2)).isEqualTo("days");
    assertThat(dayOrDaysProvider.suffixFor(DAYS, BigDecimal.valueOf(2))).isEqualTo("days");

    assertThat(dayOrDaysProvider.suffixFor(DAYS, 1)).isEqualTo("day");
    assertThat(dayOrDaysProvider.suffixFor(DAYS, BigDecimal.valueOf(1))).isEqualTo("day");

    assertThat(dayOrDaysProvider.suffixFor(DAYS, 0)).isEqualTo("days");
    assertThat(dayOrDaysProvider.suffixFor(DAYS, BigDecimal.ZERO)).isEqualTo("days");

    assertThat(dayOrDaysProvider.suffixFor(DAYS, -1)).isEqualTo("day");
    assertThat(dayOrDaysProvider.suffixFor(DAYS, BigDecimal.valueOf(-1))).isEqualTo("day");

    assertThat(dayOrDaysProvider.suffixFor(DAYS, -2)).isEqualTo("days");
    assertThat(dayOrDaysProvider.suffixFor(DAYS, BigDecimal.valueOf(-2))).isEqualTo("days");
  }
}
