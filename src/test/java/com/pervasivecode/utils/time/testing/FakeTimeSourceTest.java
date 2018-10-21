package com.pervasivecode.utils.time.testing;

import static com.google.common.truth.Truth.assertThat;
import java.time.Duration;
import java.time.Instant;
import org.junit.Before;
import org.junit.Test;

public class FakeTimeSourceTest {
  private FakeTimeSource ts;

  @Before
  public void setup() {
    ts = new FakeTimeSource();
  }

  @Test
  public void now_shouldIncreaseBy137MillisEachTime() {
    Instant i1 = ts.now();
    Instant i2 = ts.now();
    Instant i3 = ts.now();
    Instant i4 = ts.now();

    assertThat(i1).isNotNull();
    assertThat(i2).isNotNull();
    assertThat(i3).isNotNull();
    assertThat(i4).isNotNull();

    assertThat(Duration.between(i1, i2).toMillis()).isEqualTo(137L);
    assertThat(Duration.between(i2, i3).toMillis()).isEqualTo(137L);
    assertThat(Duration.between(i3, i4).toMillis()).isEqualTo(137L);
  }
}
