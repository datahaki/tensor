// code by jph
package ch.alpine.tensor.qty;

import java.time.Duration;
import java.time.LocalDateTime;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;

public enum TemporalScalars {
  ;
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
