package com.pervasivecode.utils.time;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import com.pervasivecode.utils.time.SimplePeriodicRunnerFactory;
import nl.jqno.equalsverifier.EqualsVerifier;

public class SimplePeriodicRunnerFactoryTest {
  private ScheduledExecutorService executor;

  @Test
  public void createExecutorService_shouldSetOptionsCorrectly() {
    ScheduledThreadPoolExecutor executor = SimplePeriodicRunnerFactory.createExecutorService();
    assertThat(executor.getExecuteExistingDelayedTasksAfterShutdownPolicy()).isFalse();
    assertThat(executor.getKeepAliveTime(TimeUnit.SECONDS)).isEqualTo(1);
    assertThat(executor.getRemoveOnCancelPolicy()).isTrue();
  }

  @Test(expected = NullPointerException.class)
  public void getRunnerForInterval_withNull_shouldThrow() {
    new SimplePeriodicRunnerFactory().getRunnerForInterval(null);
  }

  @Test
  public void getRunnerForInterval_with100msInterval_shouldReturnRunner() {
    new SimplePeriodicRunnerFactory().getRunnerForInterval(Duration.ofMillis(100));
  }

  @Test
  public void shutdownGracefully_whenWaitingIsNotEnough_shouldShutdownThenWaitThenShutdownNow()
      throws InterruptedException {
    executor = mock(ScheduledExecutorService.class);
    when(executor.awaitTermination(5, TimeUnit.DAYS)).thenReturn(false);
    when(executor.isTerminated()).thenReturn(false);

    SimplePeriodicRunnerFactory factory = new SimplePeriodicRunnerFactory(executor);
    factory.shutdownGracefully(5, TimeUnit.DAYS);

    verify(executor).shutdown();
    verify(executor).awaitTermination(5, TimeUnit.DAYS);
    verify(executor).shutdownNow();
  }

  @Test
  public void shutdownGracefully_whenWaitingWorks_shouldJustShutdownThenWait()
      throws InterruptedException {
    executor = mock(ScheduledExecutorService.class);
    when(executor.awaitTermination(5, TimeUnit.DAYS)).thenReturn(false);
    when(executor.isTerminated()).thenReturn(true);

    SimplePeriodicRunnerFactory factory = new SimplePeriodicRunnerFactory(executor);
    factory.shutdownGracefully(5, TimeUnit.DAYS);

    verify(executor).shutdown();
    verify(executor).awaitTermination(5, TimeUnit.DAYS);
    verify(executor, never()).shutdownNow();
  }

  @Test
  public void shutdownNow_shouldPassThroughToExecutor() {
    executor = mock(ScheduledExecutorService.class);
    SimplePeriodicRunnerFactory factory = new SimplePeriodicRunnerFactory(executor);
    factory.shutdownNow();
    verify(executor).shutdownNow();
  }

  @Test
  public void equalsAndHashCode_shouldWork() {
    EqualsVerifier.forClass(SimplePeriodicRunnerFactory.class).verify();
  }
}
