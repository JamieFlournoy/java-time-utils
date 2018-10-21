package com.pervasivecode.utils.time.api;

/**
 * Implementations provide the current time since the Unix epoch, with nanosecond precision.
 */
public interface CurrentNanosSource {
  /**
   * Obtain the current time since the Unix epoch, with nanosecond precision.
   */
  public long currentTimeNanoPrecision();
}
