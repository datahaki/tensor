// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** Remark: implementation is not consistent with Mathematica
 * for complex numbers.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Denominator.html">Denominator</a> */
public enum Denominator implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    if (scalar instanceof RationalScalar rationalScalar)
      return RealScalar.of(rationalScalar.denominator());
    return scalar.one();
  }
}
