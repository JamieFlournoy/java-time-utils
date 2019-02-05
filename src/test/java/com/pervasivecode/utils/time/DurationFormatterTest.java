package com.pervasivecode.utils.time;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HALF_DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MICROS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.ChronoUnit.WEEKS;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.truth.Truth;
import nl.jqno.equalsverifier.EqualsVerifier;

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
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(HOURS) //
        .setSmallestUnit(MILLIS) //
        .setRemainderHandling(DurationRemainderHandling.ROUND_HALF_EVEN) //
        .build();
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
    checkWeeksToMillis(formatter);
  }

  @Test
  public void format_withYearsToMillisWithFraction_shouldWork() {
    DurationFormatter formatter =
        new DurationFormatter(DurationFormat.builder(DurationFormats.getUsDefaultInstance())
            .setSmallestUnit(ChronoUnit.MILLIS).setNumFractionalDigits(3).build());

    checkFormattedDuration(formatter, Duration.ofNanos(2_521_370_223L), "2s 521.37ms");
    checkFormattedDuration(formatter, Duration.ofNanos(3_000_000_001L), "3s");

    checkFormattedDuration(formatter, Duration.ofSeconds(1), "1s");
    checkFormattedDuration(formatter, Duration.ofSeconds(1).plus(1, MILLIS), "1s 1ms");
    checkFormattedDuration(formatter, Duration.ofSeconds(1).plus(1, MICROS), "1s 0.001ms");
    checkFormattedDuration(formatter, Duration.ofSeconds(1).plus(1, NANOS), "1s");

    checkWeeksToMillis(formatter);
  }

  private void checkWeeksToMillis(DurationFormatter formatter) {
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
  public void format_withWeeksToMillisAndNoPartSeparator_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setPartDelimiter("") //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);

    checkFormattedDuration(formatter, Duration.ofMillis(17), "17ms");
    checkFormattedDuration(formatter, Duration.ofMillis(1_370_223), "22m50s223ms");
    checkFormattedDuration(formatter, Duration.ofMinutes(42022), "4w1d4h22m");
  }

  @Test
  public void format_withLongFancyFormats_shouldWork() {
    ImmutableMap<ChronoUnit, String> singularSuffixes =
        ImmutableMap.<ChronoUnit, String>builder().put(SECONDS, " second") //
            .put(MINUTES, " minute") //
            .put(HOURS, " hour") //
            .put(DAYS, " day") //
            .build();
    ImmutableMap<ChronoUnit, String> pluralSuffixes = ImmutableMap.<ChronoUnit, String>builder() //
        .put(SECONDS, " seconds") //
        .put(MINUTES, " minutes") //
        .put(HOURS, " hours") //
        .put(DAYS, " days") //
        .build();
    UnitSuffixProvider suffixProvider =
        UnitSuffixProviders.singularAndPlural(singularSuffixes, pluralSuffixes);
    DurationFormat format = DurationFormat.builder() //
        .setUnitSuffixProvider(suffixProvider) //
        .setPartDelimiter(", ") //
        .setNumberFormat(NumberFormat.getInstance(Locale.UK)) //
        .setLargestUnit(DAYS) //
        .setSmallestUnit(SECONDS) //
        .setUnitForZeroDuration(SECONDS) //
        .setNumFractionalDigits(3) //
        .setRemainderHandling(DurationRemainderHandling.ROUND_HALF_EVEN) //
        .build();

    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofSeconds(-2), "-2 seconds");
    checkFormattedDuration(formatter, Duration.ofSeconds(-1), "-1 seconds");
    checkFormattedDuration(formatter, Duration.ZERO, "0 seconds");
    checkFormattedDuration(formatter, Duration.ofMillis(999), "0.999 seconds");
    checkFormattedDuration(formatter, Duration.ofNanos(999_999_999), "1 second");
    checkFormattedDuration(formatter, Duration.ofSeconds(1), "1 second");
    checkFormattedDuration(formatter, Duration.ofMillis(1370), "1.37 seconds");
    checkFormattedDuration(formatter, Duration.ofNanos(1_000_499_999), "1 second");
    checkFormattedDuration(formatter, Duration.ofMillis(50_223), "50.223 seconds");
    checkFormattedDuration(formatter, Duration.ofMillis(1_370_223), "22 minutes, 50.223 seconds");
    checkFormattedDuration(formatter, Duration.ofMinutes(262), "4 hours, 22 minutes");
    checkFormattedDuration(formatter, Duration.ofMinutes(1702), "1 day, 4 hours, 22 minutes");
    checkFormattedDuration(formatter, Duration.ofSeconds(102170),
        "1 day, 4 hours, 22 minutes, 50 seconds");
    checkFormattedDuration(formatter, Duration.ofMillis(102_170_223),
        "1 day, 4 hours, 22 minutes, 50.223 seconds");
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
    UnitSuffixProvider suffixProvider = UnitSuffixProviders.fixedSuffixPerUnit(unitSuffixes);
    DurationFormat format = DurationFormat.builder() //
        .setUnitSuffixProvider(suffixProvider).setPartDelimiter(" ") //
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
  public void format_withJustWeeksAndSeconds_shouldWork() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(WEEKS) //
        .setSmallestUnit(SECONDS) //
        .setSuppressedUnits(ImmutableSet.of(DAYS, HALF_DAYS, HOURS, MINUTES)) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofDays(15).plusSeconds(5), "2w 86,405s");
    checkFormattedDuration(formatter, Duration.ofDays(15).plusSeconds(5).negated(), "-2w 86,405s");
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
  public void format_withDurationThatRoundsToZero_shouldFormatAsZero() {
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setLargestUnit(HOURS) //
        .setSmallestUnit(MINUTES) //
        .setUnitForZeroDuration(MINUTES) //
        .setRemainderHandling(DurationRemainderHandling.TRUNCATE) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofNanos(1), "0m");
    checkFormattedDuration(formatter, Duration.ofMillis(1), "0m");
    checkFormattedDuration(formatter, Duration.ofSeconds(59), "0m");
    checkFormattedDuration(formatter, Duration.ofMillis(59_999), "0m");
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
    UnitSuffixProvider suffixProvider =
        UnitSuffixProviders.fixedSuffixPerUnit(ImmutableMap.of(NANOS, "ns"));
    DurationFormat format = DurationFormat.builder(DurationFormats.getUsDefaultInstance()) //
        .setUnitSuffixProvider(suffixProvider) //
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
        .setUnitSuffixProvider(UnitSuffixProviders.fixedSuffixPerUnit(ImmutableMap.of(NANOS, "ns")))
        .setLargestUnit(NANOS) //
        .setSmallestUnit(NANOS) //
        .setUnitForZeroDuration(NANOS) //
        .build();
    DurationFormatter formatter = new DurationFormatter(format);
    checkFormattedDuration(formatter, Duration.ofDays(-12000), "-1,036,800,000,000,000,000ns");
    checkFormattedDuration(formatter, Duration.ofDays(-120000), "-10,368,000,000,000,000,000ns");
    checkFormattedDuration(formatter, Duration.ofDays(-1200000), "-103,680,000,000,000,000,000ns");
  }

  @Test
  public void equalsAndHashCode_shouldWork() {
    EqualsVerifier.forClass(DurationFormatter.class).verify();
  }
}
