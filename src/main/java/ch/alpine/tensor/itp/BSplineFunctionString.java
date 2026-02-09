// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** function defined over the interval [0, control.length() - 1] */
public class BSplineFunctionString extends BSplineFunctionBase {
  /** the control point are stored by reference, i.e. modifications to
   * given tensor alter the behavior of this BSplineFunction instance.
   * 
   * @param degree of polynomial basis function, non-negative integer
   * @param sequence points with at least one element
   * @return
   * @throws Exception if degree is negative, or control does not have length at least one */
  public static ScalarTensorFunction of(int degree, Tensor sequence) {
    return new BSplineFunctionString(degree, sequence);
  }

  // ---
  /** index of last control point */
  private final int last;
  /** domain of this function */
  private final Clip domain;
  /** clip for knots */
  private final Clip clip;

  public BSplineFunctionString(int degree, Tensor sequence) {
    super(LinearBinaryAverage.INSTANCE, degree, sequence);
    last = sequence.length() - 1;
    domain = Clips.positive(last);
    clip = Clips.interval( //
        domain.min().add(shift), //
        domain.max().add(shift));
  }

  @Override // from BSplineFunction
  protected int bound(int index) {
    return Integers.clip(0, last).applyAsInt(index);
  }

  @Override // from BSplineFunctionBase
  protected Scalar requireValid(Scalar scalar) {
    return domain.requireInside(scalar);
  }

  @Override // from BSplineFunctionBase
  protected Tensor project(Tensor knots) {
    return knots.maps(clip);
  }
}
