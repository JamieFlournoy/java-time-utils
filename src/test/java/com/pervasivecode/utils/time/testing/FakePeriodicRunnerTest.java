package com.pervasivecode.utils.time.testing;

import static com.google.common.truth.Truth.assertThat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Before;
import org.junit.Test;
import com.google.common.truth.Truth;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class FakePeriodicRunnerTest {
  private static final Runnable DUMMY_RUNNABLE = () -> {
  };
  private FakePeriodicRunner runner;

  @Before
  public void setup() {
    this.runner = new FakePeriodicRunner();
  }

  @Test
  public void setPeriodicTask_withStartedTask_shouldThrow() {
    try {
      runner.setPeriodicTask(DUMMY_RUNNABLE);
      runner.start();
      runner.setPeriodicTask(DUMMY_RUNNABLE);
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).contains("already scheduled");
    }
  }

  @Test
  public void setPeriodicTask_withStoppedTask_shouldWork() {
    AtomicInteger someInt = new AtomicInteger();

    runner.setPeriodicTask(() -> someInt.getAndAdd(1));
    runner.start();
    runner.runOnce();
    runner.stop();

    runner.setPeriodicTask(() -> someInt.getAndAdd(2));
    runner.start();
    runner.runOnce();
    runner.stop();

    assertThat(someInt.get()).isEqualTo(3);
  }

  @Test
  public void start_withNoTaskSet_shouldThrow() {
    try {
      runner.start();
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat().isEqualTo("No periodic task has been set.");
    }
  }

  @Test
  public void start_withTask_shouldEnableRunOnce() {
    final AtomicLong counter = new AtomicLong(1L);
    Runnable task = () -> counter.incrementAndGet();

    try {
      runner.setPeriodicTask(task);
      runner.runOnce();
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("The periodic task has not been started yet, or has been stopped.");
    }

    runner.start();
    runner.runOnce();
    assertThat(counter.get()).isEqualTo(2);
  }

  @Test
  public void start_withTaskAlreadyStarted_shouldThrow() {
    runner.setPeriodicTask(DUMMY_RUNNABLE);
    runner.start();
    try {
      runner.start();
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e.getMessage()).isEqualTo("The task has already been started.");
    }
  }

  @Test
  public void runOnce_withNoTask_shouldThrow() {
    try {
      runner.runOnce();
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("The periodic task has not been started yet, or has been stopped.");
    }
  }

  @Test
  public void runOnce_withStoppedTask_shouldThrow() {
    runner.setPeriodicTask(DUMMY_RUNNABLE);
    runner.start();
    runner.stop();
    try {
      runner.runOnce();
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat()
          .isEqualTo("The periodic task has not been started yet, or has been stopped.");
    }
  }

  @Test
  public void stop_withNoTask_shouldThrow() {
    try {
      runner.setPeriodicTask(DUMMY_RUNNABLE);
      runner.stop();
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageThat().isEqualTo("The periodic task is not running.");
    }
  }

  @Test
  public void equalsAndHashCode_shouldWork() {
    EqualsVerifier.forClass(FakePeriodicRunner.class).suppress(Warning.NONFINAL_FIELDS).verify();
  }
}
