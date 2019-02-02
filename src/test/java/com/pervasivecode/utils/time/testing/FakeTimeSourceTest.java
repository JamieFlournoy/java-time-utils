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

    Duration expectedAutoAdvance = Duration.ofMillis(137L);
    assertThat(Duration.between(i1, i2)).isEqualTo(expectedAutoAdvance);
    assertThat(Duration.between(i2, i3)).isEqualTo(expectedAutoAdvance);
    assertThat(Duration.between(i3, i4)).isEqualTo(expectedAutoAdvance);
  }

  @Test
  public void now_withAutoAdvanceFalse_shouldNotAutoAdvance() {
    ts = new FakeTimeSource(false);
    Instant i1 = ts.now();
    Instant i2 = ts.now();
    ts.advance();
    Instant i3 = ts.now();

    assertThat(Duration.between(i1, i2)).isEqualTo(Duration.ZERO);
    assertThat(Duration.between(i2, i3)).isEqualTo(Duration.ofMillis(137L));
  }

  @Test
  public void elapsedSoFar_shouldShowFakeTimeElapsedSinceInstantiation() {
    ts.now();
    assertThat(ts.elapsedSoFar()).isEqualTo(Duration.ofMillis(137L));

    ts.advance(Duration.ofSeconds(7));
    assertThat(ts.elapsedSoFar()).isEqualTo(Duration.ofMillis(7_137L));
  }
}
