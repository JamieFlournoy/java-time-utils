package com.pervasivecode.utils.time.impl;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;
import com.google.common.truth.Truth;

public class SimplePeriodicRunnerTest {
  private static final Runnable DUMMY_RUNNABLE = () -> {};

  private static final Duration TEST_PERIOD_DURATION = Duration.ofSeconds(37);

  @Rule
  public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  @Mock
  private ScheduledExecutorService executor;
  @Mock
  private ScheduledFuture<?> future;
  private SimplePeriodicRunner runner;

  /**
   * Stub the call to
   * {@link ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)} which is
   * called by {@link SimplePeriodicRunner#start()}.
   */
  private void expectTaskToBeScheduled() {
    long durationMillis = TEST_PERIOD_DURATION.toMillis();
    // The executor has to return something when the test calls scheduleAtFixedRate.
    when(executor.scheduleAtFixedRate(Mockito.any(Runnable.class), eq(durationMillis),
        eq(durationMillis), eq(TimeUnit.MILLISECONDS))).then((Answer<?>) (i) -> future);
  }

  @Before
  public void setup() {
    this.runner = new SimplePeriodicRunner(executor, TEST_PERIOD_DURATION);
  }

  @Test
  public void setPeriodicTask_withStartedTask_shouldThrow() {
    expectTaskToBeScheduled();
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
  public void start_withNoTaskSet_shouldThrow() {
    try {
      runner.start();
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("No periodic task has been set.");
    }
  }

  @Test
  public void start_withTask_shouldScheduleUsingExecutor() {
    expectTaskToBeScheduled(); 
    runner.setPeriodicTask(DUMMY_RUNNABLE);
    runner.start();
    // STRICT_STUBS will verify that the stubs set in expectTaskToBeScheduled were actually used, so
    // there's no need to separately verify interactions with the mock here.
  }

  /** Ensure that a single task isn't scheduled to be called multiple times per interval. */
  @Test
  public void start_withTaskAlreadyStarted_shouldThrow() {
    expectTaskToBeScheduled(); 
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
  public void stop_withNoTask_shouldThrow() {
    try {
      runner.setPeriodicTask(DUMMY_RUNNABLE);
      runner.stop();
      Truth.assert_().fail("Expected IllegalStateException.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("The periodic task has not been started yet, or has been stopped.");
    }
  }

  @Test
  public void stop_withTask_shouldCancelTask() {
    expectTaskToBeScheduled();
    when(future.cancel(false)).thenReturn(true);

    runner.setPeriodicTask(DUMMY_RUNNABLE);
    runner.start();
    runner.stop();
  }
}
