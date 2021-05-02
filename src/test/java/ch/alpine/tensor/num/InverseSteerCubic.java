// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** safety critical code used for the gokart steering system */
/* package */ class InverseSteerCubic implements ScalarUnaryOperator {
  private final Scalar b;
  private final Scalar d;

  /** @param b linear coefficient
   * @param d cubic coefficient */
  public InverseSteerCubic(Scalar b, Scalar d) {
    this.b = b;
    this.d = d;
  }

  @Override
  public Scalar apply(Scalar y) {
    return Roots.of(Tensors.of(y.negate(), b, RealScalar.ZERO, d)).Get(1);
  }
}
