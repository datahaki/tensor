// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Mod;

/** function defined for all real scalars */
/* package */ class BSplineFunctionCyclic extends BSplineFunction {
  private final int length;
  /** periodic */
  private final Mod mod;

  public BSplineFunctionCyclic(int degree, Tensor control) {
    super(degree, control);
    length = control.length();
    mod = Mod.function(length);
  }

  @Override // from BSplineFunction
  Scalar domain(Scalar scalar) {
    return mod.apply(scalar);
  }

  @Override // from BSplineFunction
  Tensor knots(Tensor knots) {
    return knots;
  }

  @Override // from BSplineFunction
  int bound(int index) {
    return Math.floorMod(index, length);
  }
}
