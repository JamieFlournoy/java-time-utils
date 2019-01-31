package com.pervasivecode.utils.time;

import static com.google.common.truth.Truth.assertThat;
import java.text.NumberFormat;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.junit.Test;
import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;

public class DurationFormatTest {
  private static DurationFormat.Builder validBuilder() {
    return DurationFormat.builder().setUnitSuffixes( //
        ImmutableMap.of(ChronoUnit.HOURS, "h", //
            ChronoUnit.MINUTES, "m", //
            ChronoUnit.SECONDS, "s", //
            ChronoUnit.MILLIS, "ms")) //
        .setPartDelimiter(" ") //
        .setNumberFormat(NumberFormat.getInstance(Locale.US))
        .setLargestUnit(ChronoUnit.HOURS) //
        .setSmallestUnit(ChronoUnit.MINUTES) //
        .setUnitForZeroDuration(ChronoUnit.MINUTES) //
        .setNumFractionalDigits(1);
  }

  @Test
  public void build_withNoSuffixes_shouldThrow() {
    try {
      validBuilder() //
          .setUnitSuffixes(ImmutableMap.of()) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("suffixes");
    }
  }

  @Test
  public void build_withNoUnits_shouldThrow() {
    try {
      validBuilder() //
          .setLargestUnit(ChronoUnit.MINUTES) //
          .setSmallestUnit(ChronoUnit.HOURS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("units");
    }

    try {
      validBuilder() //
          .setLargestUnit(ChronoUnit.MILLIS) //
          .setSmallestUnit(ChronoUnit.SECONDS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("units");
    }
  }

  @Test
  public void build_withFewerThanZeroUnits_shouldThrow() {
    try {
      validBuilder() //
          .setLargestUnit(ChronoUnit.SECONDS) //
          .setSmallestUnit(ChronoUnit.HOURS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("units");
    }
  }

  @Test
  public void build_withUnitsMissingSuffixes_shouldThrow() {
    try {
      validBuilder() //
          .setUnitSuffixes(ImmutableMap.of(ChronoUnit.MINUTES, "min")) //
          .setLargestUnit(ChronoUnit.MINUTES) //
          .setSmallestUnit(ChronoUnit.SECONDS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("suffix");
      assertThat(iae).hasMessageThat().contains("SECONDS");
    }
  }

  @Test
  public void build_withUnitForZeroDurationNotInSmallestToLargestUnitRange_shouldThrow() {
    try {
      validBuilder() //
          .setLargestUnit(ChronoUnit.MINUTES) //
          .setSmallestUnit(ChronoUnit.MILLIS) //
          .setUnitForZeroDuration(ChronoUnit.HOURS) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("Zero");
    }
  }
  
  @Test
  public void build_withNegativeFractionalDigits_shouldThrow() {
    try {
      validBuilder() //
          .setNumFractionalDigits(-1) //
          .build();
      Truth.assert_().fail("Expected an exception here.");
    } catch (IllegalArgumentException iae) {
      assertThat(iae).hasMessageThat().contains("digits");
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
