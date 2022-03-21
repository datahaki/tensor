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
 * <a href="https://reference.wolfram.com/language/ref/Numerator.html">Numerator</a> */
public enum Numerator implements ScalarUnaryOperator {
  FUNCTION;

  @Override
  public Scalar apply(Scalar scalar) {
    return scalar instanceof RationalScalar //
        ? RealScalar.of(((RationalScalar) scalar).numerator())
        : scalar;
  }
}
