// code by jph
package ch.alpine.tensor.sca.exp;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.PackageTestAccess;
import ch.alpine.tensor.sca.N;

/** gives the exponential of a {@link Scalar} that implements {@link ExpInterface}.
 * Supported types include {@link RealScalar}, and {@link ComplexScalar}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Exp.html">Exp</a>
 * 
 * @see ExpInterface
 * @see Log */
public enum Exp implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof ExpInterface expInterface)
      return expInterface.exp();
    return series(ExactScalarQ.of(scalar) ? N.DOUBLE.apply(scalar) : scalar);
  }

  @PackageTestAccess
  static Scalar series(Scalar x) {
    Scalar xn0 = x.zero();
    Scalar xn1 = x.one();
    Scalar add = x;
    for (int index = 0; !xn0.equals(xn1);) {
      xn0 = xn1;
      add = add.multiply(x).divide(RealScalar.of(++index));
      xn1 = xn1.add(add);
    }
    return xn1;
  }
}
