// code by jph
package ch.alpine.tensor.num;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Mod;

/** <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/QuotientRemainder.html">QuotientRemainder</a>
 * 
 * @see NumberDecompose */
public record QuotientRemainder(Scalar quotient, Scalar remainder) implements Serializable {
  public static QuotientRemainder of(Scalar num, Scalar den) {
    return new QuotientRemainder( //
        Floor.FUNCTION.apply(num.divide(den)), //
        Mod.function(den).apply(num));
  }

  public static QuotientRemainder of(Number num, Number den) {
    return of(RealScalar.of(num), RealScalar.of(den));
  }
}
