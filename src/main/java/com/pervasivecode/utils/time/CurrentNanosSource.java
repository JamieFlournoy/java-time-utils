package com.pervasivecode.utils.time;

/** An object that provides the current time since the Unix epoch, with nanosecond precision. */
public interface CurrentNanosSource {

  /**
   * Obtain the current time since the Unix epoch, with nanosecond precision.
   * @return The number of nanoseconds that have elapsed since the Unix epoch.
   */
  public long currentTimeNanoPrecision();
}
