// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Clips;

/** inspired by Mathematica's documentation of Gamma */
/* package */ class NewtonDemo extends BivariateEvaluation {
  private static final int DEPTH = 2;

  public static BivariateEvaluation of(Tensor coeffs) {
    return new NewtonDemo(coeffs);
  }

  // ---
  private final ScalarUnaryOperator scalarUnaryOperator;

  private NewtonDemo(Tensor coeffs) {
    super(Clips.absolute(2.0), Clips.absolute(2.0));
    scalarUnaryOperator = NewtonScalarMethod.polynomial(coeffs).iteration;
  }

  @Override
  protected Scalar function(Scalar re, Scalar im) {
    return Arg.of(Nest.of(scalarUnaryOperator, ComplexScalar.of(re, im), DEPTH));
  }

  public static void main(String[] args) throws Exception {
    StaticHelper.export(of(Tensors.vector(1, 5, 0, 1)), Polynomial.class, ColorDataGradients.PARULA);
  }
}
// depth3
// Series.of(Tensors.vector(1, 5, 0, 1)), //
// Series.of(Tensors.vector(2, 1, 1))).iteration;
