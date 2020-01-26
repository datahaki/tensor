// code by jph
package ch.ethz.idsc.tensor.num;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Remark: implementation is not consistent with Mathematica
 * for complex numbers.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Denominator.html">Denominator</a> */
public enum Denominator implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof RationalScalar) {
      RationalScalar rationalScalar = (RationalScalar) scalar;
      return RealScalar.of(rationalScalar.denominator());
    }
    return RealScalar.ONE;
  }
}
