// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.sca.Mod;

/** function defined for all real scalars */
public class BSplineFunctionCyclic extends BSplineFunctionBase {
  /** function is periodic every interval [0, control.length())
   * 
   * @param degree non-negative
   * @param sequence with at least one element
   * @return function defined for all real scalars not constrained to a finite interval
   * @throws Exception if degree is negative, or control does not have length at least one */
  public static ScalarTensorFunction of(int degree, Tensor sequence) {
    return new BSplineFunctionCyclic(degree, sequence);
  }

  // ---
  private final int length;
  /** periodic */
  private final Mod mod;

  public BSplineFunctionCyclic(int degree, Tensor control) {
    super(LinearBinaryAverage.INSTANCE, degree, control);
    length = control.length();
    mod = Mod.function(length);
  }

  @Override // from BSplineFunction
  protected Scalar requireValid(Scalar scalar) {
    return mod.apply(scalar);
  }

  @Override // from BSplineFunction
  protected Tensor project(Tensor knots) {
    return knots;
  }

  @Override // from BSplineFunction
  protected int bound(int index) {
    return Math.floorMod(index, length);
  }
}
