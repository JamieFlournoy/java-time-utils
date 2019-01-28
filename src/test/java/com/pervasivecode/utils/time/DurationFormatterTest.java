package com.pervasivecode.utils.time;

import static com.google.common.truth.Truth.assertThat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.Test;

public class DurationFormatterTest {
  private static void checkDurationFormat(Duration duration, String expected) {
    assertThat(DurationFormatter.US().format(duration)).isEqualTo(expected);
  }

  private static void checkDurationFormat(Duration duration, ChronoUnit largestUnit,
      String expected) {
    assertThat(DurationFormatter.US().format(duration, largestUnit)).isEqualTo(expected);
  }

  private static void checkDurationFormat(Duration duration, ChronoUnit largestUnit,
      ChronoUnit smallestUnit, String expected) {
    assertThat(DurationFormatter.US().format(duration, largestUnit, smallestUnit))
        .isEqualTo(expected);
  }

  @Test
  public void format_duration_withSeveralMillis_shouldUseMillis() {
    checkDurationFormat(Duration.ofMillis(17), "17ms");
  }

  @Test
  public void format_duration_withZero_shouldUseSeconds() {
    checkDurationFormat(Duration.ofSeconds(0), "0s");
  }

  @Test
  public void format_duration_withSeveralMillisAndLargestUnitMinutes_shouldUseMillis() {
    checkDurationFormat(Duration.ofMillis(17L), ChronoUnit.MINUTES, "17ms");
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
  public void format_duration_withWholeMinutes_shouldUseMinutes() {
    checkDurationFormat(Duration.ofMinutes(15), ChronoUnit.HOURS, ChronoUnit.MINUTES, "15m");
  }

  @Test
  public void format_duration_withWholeMinutesAndLargestUnitMillis_shouldUseMillis() {
    checkDurationFormat(Duration.ofMinutes(15), ChronoUnit.MILLIS, "900000ms");
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
  public void format_duration_withBillionsOfMillisAndLargestUnitMinutes_shouldUseMinutesSeconds() {
    checkDurationFormat(Duration.ofMillis(2_521_370_223L), ChronoUnit.MINUTES, "42022m 50s");
  }

  @Test
  public void format_duration_withBillionsOfMillisAndOnlyUnitMinutes_shouldUseMinutesSeconds() {
    checkDurationFormat(Duration.ofMillis(2_521_370_223L), ChronoUnit.MINUTES, ChronoUnit.MINUTES,
        "42022m");
  }

  @Test
  public void format_durations_inJavadocComments_shouldFormatAsDescribed() {
    checkDurationFormat(Duration.ofMillis(12345), "12.345s");
    checkDurationFormat(Duration.ofMillis(345), "345ms");
    checkDurationFormat(Duration.ofSeconds(3600), "1h 0m 0s");
    checkDurationFormat(Duration.ofSeconds(86400 - 1), "23h 59m 59s");
  }
}
