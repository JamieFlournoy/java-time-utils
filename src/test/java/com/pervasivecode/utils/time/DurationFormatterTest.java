package com.pervasivecode.utils.time;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;

public class DurationFormatterTest {
  private static void checkFormattedDuration(DurationFormatter formatter, Duration duration,
      String expectedFormattedValue) {
    Truth.assertThat(formatter.format(duration)).isEqualTo(expectedFormattedValue);
  }

  @Test
  public void format_negativeDuration_withWeeksToMillis_shouldWork() {
    DurationFormatter formatter = new DurationFormatter(DurationFormats.getUsDefaultInstance());

    checkFormattedDuration(formatter, Duration.ofSeconds(-2521370), "-4w 1d 4h 22m 50s");
    checkFormattedDuration(formatter, Duration.ofMillis(-2_521_370_223L),
        "-4w 1d 4h 22m 50s 223ms");
    checkFormattedDuration(formatter, Duration.ofHours(-1), "-1h");
    checkFormattedDuration(formatter, Duration.ofHours(-25), "-1d 1h");
  }

  @Test
  public void format_withHoursToMillisWithRounding_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance())
        .setRemainderHandling(RemainderHandling.ROUND).build();
    DurationFormatter formatter = new DurationFormatter(format);

    checkFormattedDuration(formatter, Duration.ofMillis(1), "1ms");
    checkFormattedDuration(formatter, Duration.ofMillis(999), "999ms");
    checkFormattedDuration(formatter, Duration.ofNanos(499999), "0s");
    checkFormattedDuration(formatter, Duration.ofNanos(500000), "0s");
    checkFormattedDuration(formatter, Duration.ofNanos(500001), "1ms");
    checkFormattedDuration(formatter, Duration.ofNanos(999000), "1ms");
    checkFormattedDuration(formatter, Duration.ofNanos(1500000), "2ms");
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

  @Test
  public void format_withWeeksToFractionalSeconds_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setSmallestUnit(SECONDS) //
        .setNumFractionalDigits(3) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofMillis(1370), "1.37s");
    checkFormattedDuration(formatter, Duration.ofMillis(1_370_223), "22m 50.223s");
  }

  @Test
  public void format_forGermany_withWeeksToFractionalSeconds_shouldWork() {
    // Abbreviations taken from https://german.stackexchange.com/questions/14895/abbreviating-time
    ImmutableMap<ChronoUnit, String> unitSuffixes = ImmutableMap.<ChronoUnit, String>builder() //
        .put(SECONDS, "sek.") //
        .put(MINUTES, "min.") //
        .put(HOURS, "Std.") //
        .put(DAYS, "Tg.") //
        .build();

    DurationFormat format = DurationFormat.builder() //
        .setUnitSuffixes(unitSuffixes) //
        .setPartDelimiter(" ") //
        .setNumberFormat(NumberFormat.getInstance(Locale.GERMANY)) //
        .setLargestUnit(DAYS) //
        .setSmallestUnit(SECONDS) //
        .setUnitForZeroDuration(SECONDS) //
        .setNumFractionalDigits(3) //
        .build();

    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ZERO, "0sek.");
    checkFormattedDuration(formatter, Duration.ofMillis(1370), "1,37sek.");
    checkFormattedDuration(formatter, Duration.ofMillis(50_223), "50,223sek.");
    checkFormattedDuration(formatter, Duration.ofMillis(1_370_223), "22min. 50,223sek.");
    checkFormattedDuration(formatter, Duration.ofMinutes(262), "4Std. 22min.");
    checkFormattedDuration(formatter, Duration.ofMinutes(1702), "1Tg. 4Std. 22min.");
    checkFormattedDuration(formatter, Duration.ofSeconds(102170), "1Tg. 4Std. 22min. 50sek.");
    checkFormattedDuration(formatter, Duration.ofMillis(102_170_223),
        "1Tg. 4Std. 22min. 50,223sek.");
  }

  @Test
  public void format_withMinutesToMillis_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(MINUTES) //
        .setSmallestUnit(MILLIS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofMillis(2_521_370_223L), "42,022m 50s 223ms");
  }

  @Test
  public void format_withJustWholeMillis_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(MILLIS) //
        .setSmallestUnit(MILLIS) //
        .setUnitForZeroDuration(MILLIS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ZERO, "0ms");
    checkFormattedDuration(formatter, Duration.ofMinutes(15), "900,000ms");
  }

  @Test
  public void format_withJustWholeMinutes_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(MINUTES) //
        .setSmallestUnit(MINUTES) //
        .setUnitForZeroDuration(MINUTES) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ZERO, "0m");
    checkFormattedDuration(formatter, Duration.ofMillis(2_521_370_223L), "42,022m");
  }

  @Test
  public void format_withJustWholeDays_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(DAYS) //
        .setSmallestUnit(DAYS) //
        .setUnitForZeroDuration(DAYS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofHours(10), "0d");
    checkFormattedDuration(formatter, Duration.ofDays(8), "8d");
  }

  @Test
  public void format_aVeryLargeNumberOfNanos_withJustNanos_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setUnitSuffixes(ImmutableMap.of(NANOS, "ns")) //
        .setLargestUnit(NANOS) //
        .setSmallestUnit(NANOS) //
        .setUnitForZeroDuration(NANOS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofDays(0), "0ns");
    checkFormattedDuration(formatter, Duration.ofDays(12000), "1,036,800,000,000,000,000ns");
    checkFormattedDuration(formatter, Duration.ofDays(120000), "10,368,000,000,000,000,000ns");
    checkFormattedDuration(formatter, Duration.ofDays(1200000), "103,680,000,000,000,000,000ns");
  }

  @Test
  public void format_aVeryLargeNegativeNumberOfNanos_withJustNanos_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setUnitSuffixes(ImmutableMap.of(NANOS, "ns")) //
        .setLargestUnit(NANOS) //
        .setSmallestUnit(NANOS) //
        .setUnitForZeroDuration(NANOS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofDays(-12000), "-1,036,800,000,000,000,000ns");
    checkFormattedDuration(formatter, Duration.ofDays(-120000), "-10,368,000,000,000,000,000ns");
    checkFormattedDuration(formatter, Duration.ofDays(-1200000), "-103,680,000,000,000,000,000ns");
  }
}
