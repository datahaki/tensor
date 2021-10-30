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
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/** inspired by Mathematica's documentation of Gamma */
/* package */ class NewtonDemo implements BivariateEvaluation {
  private static final int DEPTH = 2;
  private final ScalarUnaryOperator scalarUnaryOperator;

  public NewtonDemo(Tensor coeffs) {
    scalarUnaryOperator = NewtonScalarMethod.polynomial(coeffs).iteration;
  }

  @Override
  public Scalar apply(Scalar re, Scalar im) {
    return Arg.of(Nest.of(scalarUnaryOperator, ComplexScalar.of(re, im), DEPTH));
  }

  @Override
  public Clip clipX() {
    return Clips.absolute(2.0);
  }

  @Override
  public Clip clipY() {
    return Clips.absolute(2.0);
  }

  public static void main(String[] args) throws Exception {
    StaticHelper.export(new NewtonDemo(Tensors.vector(1, 5, 0, 1)), Polynomial.class, ColorDataGradients.PARULA);
  }
}
// depth3
// Polynomial.of(Tensors.vector(1, 5, 0, 1)), //
// Polynomial.of(Tensors.vector(2, 1, 1))).iteration;
