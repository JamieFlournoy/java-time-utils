package com.pervasivecode.utils.time.testing;

import static com.google.common.truth.Truth.assertThat;
import org.junit.Test;


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
}
