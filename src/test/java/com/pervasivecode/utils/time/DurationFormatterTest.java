package com.pervasivecode.utils.time;

import static com.google.common.truth.Truth.assertThat;
import java.time.Duration;
import org.junit.Test;

public class DurationFormatterTest {
  private static void checkDurationFormat(Duration duration, String expected) {
    assertThat(DurationFormatter.US().format(duration)).isEqualTo(expected);
  }

  @Test
  public void format_duration_withSeveralMillis_shouldUseMillis() {
    checkDurationFormat(Duration.ofMillis(17), "17ms");
  }

  @Test
  public void format_duration_withHundredsOfMillis_shouldUseMillis() {
    checkDurationFormat(Duration.ofMillis(137), "137ms");
  }

  @Test
  public void format_duration_withThousandsOfMillis_shouldUseSeconds() {
    checkDurationFormat(Duration.ofMillis(1370), "1.370s");
  }

  @Test
  public void format_duration_withMillionsOfMillis_shouldUseMinutesAndSeconds() {
    checkDurationFormat(Duration.ofMillis(1_370_223), "22m 50s");
  }

  @Test
  public void format_duration_withBillionsOfMillis_shouldUseHoursMinutesAndSeconds() {
    checkDurationFormat(Duration.ofMillis(2_521_370_223L), "4w 1d 4h 22m 50s");
  }

  @Test
  public void format_durations_inJavadocComments_shouldFormatAsDescribed() {
    checkDurationFormat(Duration.ofMillis(12345), "12.345s");
    checkDurationFormat(Duration.ofMillis(345), "345ms");
    checkDurationFormat(Duration.ofSeconds(3600), "1h 0m 0s");
    checkDurationFormat(Duration.ofSeconds(86400 - 1), "23h 59m 59s");
  }
}
