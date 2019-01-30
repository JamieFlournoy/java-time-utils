package com.pervasivecode.utils.time;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.time.Duration;
import org.junit.Ignore;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;

public class DurationFormatterTest {
  private static void checkFormattedDuration(DurationFormatter formatter, Duration duration,
      String expectedFormattedValue) {
    Truth.assertThat(formatter.format(duration)).isEqualTo(expectedFormattedValue);
  }

  @Test
  public void format_withWeeksToMillis_shouldWork() {
    DurationFormatter formatter = new DurationFormatter(DurationFormats.getUsDefaultInstance());

    checkFormattedDuration(formatter, Duration.ofMillis(17), "17ms");
    checkFormattedDuration(formatter, Duration.ofMillis(137), "137ms");
    checkFormattedDuration(formatter, Duration.ofMillis(1370), "1s 370ms");
    checkFormattedDuration(formatter, Duration.ofMillis(1_370_223), "22m 50s 223ms");
    checkFormattedDuration(formatter, Duration.ofMinutes(42022), "4w 1d 4h 22m");
    checkFormattedDuration(formatter, Duration.ofSeconds(2521370), "4w 1d 4h 22m 50s");
    checkFormattedDuration(formatter, Duration.ofMillis(2_521_370_223L), "4w 1d 4h 22m 50s 223ms");

    checkFormattedDuration(formatter, Duration.ZERO, "0s");

    checkFormattedDuration(formatter, Duration.ofMinutes(1), "1m");
    checkFormattedDuration(formatter, Duration.ofMinutes(15), "15m");

    checkFormattedDuration(formatter, Duration.ofHours(1), "1h");

    checkFormattedDuration(formatter, Duration.ofDays(8), "1w 1d");
    checkFormattedDuration(formatter, Duration.ofDays(17), "2w 3d");
  }

  @Ignore // TODO handle fractions.
  @Test
  public void format_withWeeksToFractionalSeconds_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setSmallestUnit(SECONDS) //
        .setNumFractionalDigits(3) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofMillis(1370), "1.370s");
    checkFormattedDuration(formatter, Duration.ofMillis(1_370_223), "22m 50.223s");
  }

  @Test
  public void format_withMinutesToMillis_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(MINUTES) //
        .setSmallestUnit(MILLIS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofMillis(2_521_370_223L), "42022m 50s 223ms");
  }

  @Test
  public void format_withJustWholeMillis_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(MILLIS) //
        .setSmallestUnit(MILLIS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofMinutes(15), "900000ms");
  }

  @Test
  public void format_withJustWholeMinutes_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(MINUTES) //
        .setSmallestUnit(MINUTES) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofMillis(2_521_370_223L), "42022m");
  }

  @Test
  public void format_withJustWholeDays_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(DAYS) //
        .setSmallestUnit(DAYS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofHours(10), "0d");
    checkFormattedDuration(formatter, Duration.ofDays(8), "8d");
  }

  @Test
  public void format_aVeryLargeNumberOfNanos_withJustNanos_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setUnitSuffixes(ImmutableMap.of(NANOS, "ns")).setLargestUnit(NANOS) //
        .setSmallestUnit(NANOS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofDays(0), "0ns");
    checkFormattedDuration(formatter, Duration.ofDays(12000), "1036800000000000000ns");
    checkFormattedDuration(formatter, Duration.ofDays(120000), "10368000000000000000ns");
  }

  // TODO Handle RemainderHandling other than TRUNCATE.

  // TODO Handle negative durations.
}
