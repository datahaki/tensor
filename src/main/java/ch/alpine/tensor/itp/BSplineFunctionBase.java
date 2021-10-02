// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.sca.Floor;

public abstract class BSplineFunctionBase extends BSplineFunction {
  protected BSplineFunctionBase(BinaryAverage binaryAverage, int degree, Tensor sequence) {
    super(binaryAverage, degree, sequence);
  }

  @Override // from ScalarTensorFunction
  public final Tensor apply(Scalar scalar) {
    scalar = requireValid(scalar).add(shift);
    return deBoor(Floor.intValueExact(scalar)).apply(scalar);
  }

  @Override
  protected final Tensor knots(int k) {
    return project(Range.of(-degree + 1 + k, degree + 1 + k));
  }

  /** @param scalar
   * @return scalar guaranteed to be in the evaluation domain
   * @throws Exception if given scalar was outside permitted range */
  protected abstract Scalar requireValid(Scalar scalar);

  /** @param knots
   * @return */
  protected abstract Tensor project(Tensor knots);
}
