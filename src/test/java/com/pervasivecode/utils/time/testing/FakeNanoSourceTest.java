package com.pervasivecode.utils.time.testing;

import static com.google.common.truth.Truth.assertThat;
import static nl.jqno.equalsverifier.Warning.NONFINAL_FIELDS;
import org.junit.Test;
import nl.jqno.equalsverifier.EqualsVerifier;


public class FakeNanoSourceTest {
  @Test
  public void currentTimeNanoPrecision_calledSeveralTimes_shouldIncrease() {
    FakeNanoSource nanoSource = new FakeNanoSource();
    long initialNanos = nanoSource.currentTimeNanoPrecision();
    assertThat(initialNanos).isNotEqualTo(0L);
    long secondNanos = nanoSource.currentTimeNanoPrecision();
    assertThat(secondNanos).isGreaterThan(initialNanos);
    long thirdNanos = nanoSource.currentTimeNanoPrecision();
    assertThat(thirdNanos).isGreaterThan(secondNanos);
  }

  @Test
  public void incrementTimeNanos_shouldAdd() {
    FakeNanoSource nanoSource = new FakeNanoSource();
    long firstNanos = nanoSource.currentTimeNanoPrecision();
    long amountToIncrement = 456L;
    nanoSource.incrementTimeNanos(amountToIncrement);
    long secondNanos = nanoSource.currentTimeNanoPrecision();
    assertThat(secondNanos).isAtLeast(firstNanos + amountToIncrement);
  }

  @Test
  public void equalsAndHashCode_shouldWork() {
    EqualsVerifier.forClass(FakeNanoSource.class).suppress(NONFINAL_FIELDS).verify();
  }
}
