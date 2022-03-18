// code by jph
package ch.alpine.tensor.jet;

import java.time.Duration;
import java.time.LocalDateTime;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

public enum TemporalScalars {
  ;
  /** parsing function
   * 
   * Examples:
   * PT-68H-7M-14.123236987S parses to {@link DurationScalar}
   * 2020-12-20T04:30:03.125239876 parses to {@link DateTimeScalar}
   * 
   * @param string
   * @return
   * @see LocalDateTime
   * @see Duration
   * @see Scalars#fromString(String) */
  public static Scalar fromString(String string) {
    try {
      return new DateTimeScalar(LocalDateTime.parse(string));
    } catch (Exception exception) {
      // ---
    }
    try {
      return new DurationScalar(Duration.parse(string));
    } catch (Exception exception) {
      // ---
    }
    return Scalars.fromString(string);
  }
}
