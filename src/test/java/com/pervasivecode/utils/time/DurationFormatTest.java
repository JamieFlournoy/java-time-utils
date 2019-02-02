package com.pervasivecode.utils.time;

import static com.google.common.truth.Truth.assertThat;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.text.NumberFormat;
import java.util.Locale;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.truth.Truth;

public class DurationFormatTest {
  private static DurationFormat.Builder validBuilder() {
    UnitSuffixProvider suffixProvider = UnitSuffixProviders.fixedSuffixPerUnit( //
        ImmutableMap.of(HOURS, "h", //
            MINUTES, "m", //
            SECONDS, "s", //
            MILLIS, "ms"));
    return DurationFormat.builder() //
        .setUnitSuffixProvider(suffixProvider) //
        .setPartDelimiter(" ") //
        .setNumberFormat(NumberFormat.getInstance(Locale.US)) //
        .setLargestUnit(HOURS) //
        .setSmallestUnit(MINUTES) //
        .setUnitForZeroDuration(MINUTES) //
        .setNumFractionalDigits(1);
  }

  @Test
  public void build_withNoSuffixes_shouldThrow() {
    try {
      UnitSuffixProvider suffixProvider = UnitSuffixProviders.fixedSuffixPerUnit(ImmutableMap.of());
      validBuilder() //
          .setUnitSuffixProvider(suffixProvider) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("suffix");
    }
  }

  @Test
  public void build_withNoUnits_shouldThrow() {
    try {
      validBuilder() //
          .setLargestUnit(MINUTES) //
          .setSmallestUnit(HOURS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("units");
    }

    try {
      validBuilder() //
          .setLargestUnit(MILLIS) //
          .setSmallestUnit(SECONDS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("units");
    }

    try {
      validBuilder() //
          .setLargestUnit(HOURS) //
          .setSmallestUnit(SECONDS) //
          .setUnitForZeroDuration(SECONDS) //
          .setSuppressedUnits(ImmutableSet.of(HOURS, MINUTES, SECONDS)) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("suppressedUnits");
    }
  }

  @Test
  public void build_withFewerThanZeroUnits_shouldThrow() {
    try {
      validBuilder() //
          .setLargestUnit(SECONDS) //
          .setSmallestUnit(HOURS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("units");
    }
  }

  @Test
  public void build_withUnitsMissingSuffixes_shouldThrow() {
    try {
      UnitSuffixProvider suffixProvider =
          UnitSuffixProviders.fixedSuffixPerUnit(ImmutableMap.of(MINUTES, "min"));
      validBuilder() //
          .setUnitSuffixProvider(suffixProvider) //
          .setLargestUnit(MINUTES) //
          .setSmallestUnit(SECONDS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("suffix");
      assertThat(ise).hasMessageThat().contains("Seconds");
    }
  }

  @Test
  public void build_withUnitForZeroDurationNotInSmallestToLargestUnitRange_shouldThrow() {
    try {
      validBuilder() //
          .setLargestUnit(MINUTES) //
          .setSmallestUnit(MILLIS) //
          .setUnitForZeroDuration(HOURS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("Zero");
    }
  }

  @Test
  public void build_withNegativeFractionalDigits_shouldThrow() {
    try {
      validBuilder() //
          .setNumFractionalDigits(-1) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalStateException ise) {
      assertThat(ise).hasMessageThat().contains("digits");
    }
  }

  @Test
  public void build_withSaneValues_shouldWork() {
    validBuilder().build();
  }

  @Test
  public void builder_withInstance_shouldBuildEqualInstance() {
    DurationFormat format = validBuilder().build();
    DurationFormat otherFormat = DurationFormat.builder(format).build();
    assertThat(otherFormat).isEqualTo(format);
  }
}
