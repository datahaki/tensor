// code by jph
package ch.alpine.tensor.jet;

import java.time.Duration;
import java.time.LocalDateTime;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Floor;

/** @see UnitSystem#SI() */
public enum TemporalScalars {
  ;
  /** parsing function
   * 
   * Examples:
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
    return Scalars.fromString(string);
  }

  /** @param scalar with unit "s" or compatible
   * @return */
  public static Duration duration(Scalar scalar) {
    scalar = DateTimeScalar.TO_SECONDS.apply(scalar);
    Scalar integral = Floor.FUNCTION.apply(scalar);
    return Duration.ofSeconds( //
        Scalars.longValueExact(integral), //
        scalar.subtract(integral).multiply(DateTimeScalar.NANOS).number().longValue());
  }

  /** @param duration
   * @return quantity with value equals to the number of seconds encoded in given duration and unit "s" */
  public static Scalar seconds(Duration duration) {
    Scalar seconds = RealScalar.of(duration.getSeconds());
    Scalar faction = RationalScalar.of(duration.getNano(), DateTimeScalar.NANOS_LONG);
    return Quantity.of(seconds.add(faction), DateTimeScalar.UNIT_S);
  }
}
