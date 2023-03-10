// code by jph
package ch.alpine.tensor.sca.gam;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.exp.Exp;

/** Reference:
 * https://en.wikipedia.org/wiki/Gompertz_function */
public record GompertzFunction(Scalar a, Scalar b, Scalar c) implements ScalarUnaryOperator {
  public static ScalarUnaryOperator of(Number a, Number b, Number c) {
    return new GompertzFunction(RealScalar.of(a), RealScalar.of(b), RealScalar.of(c));
  }

  @Override
  public Scalar apply(Scalar t) {
    return Exp.FUNCTION.apply( //
        Exp.FUNCTION.apply(t.multiply(c).negate()) //
            .multiply(b).negate())
        .multiply(a);
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("GompertzFunction", a(), b(), c());
  }
}
