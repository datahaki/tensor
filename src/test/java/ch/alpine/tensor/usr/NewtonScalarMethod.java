// code by jph
package ch.alpine.tensor.usr;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.ply.Polynomial;

/** https://en.wikipedia.org/wiki/Newton%27s_method */
/* package */ class NewtonScalarMethod implements ScalarUnaryOperator {
  public static NewtonScalarMethod polynomial(Tensor coeffs) {
    Polynomial polynomial = Polynomial.of(coeffs);
    return new NewtonScalarMethod( //
        polynomial, //
        polynomial.derivative());
  }

  // ---
  private final ScalarUnaryOperator function;
  public final ScalarUnaryOperator iteration;

  public NewtonScalarMethod(ScalarUnaryOperator function, ScalarUnaryOperator derivative) {
    this.function = function;
    iteration = z -> z.subtract(function.apply(z).divide(derivative.apply(z)));
  }

  @Override // from ScalarUnaryOperator
  public Scalar apply(Scalar prev_root) {
    Scalar prev_error = Abs.of(function.apply(prev_root));
    while (true) {
      Scalar next_root = iteration.apply(prev_root);
      Scalar next_error = Abs.of(function.apply(next_root));
      if (Scalars.lessEquals(prev_error, next_error))
        return prev_root;
      prev_error = next_error;
      prev_root = next_root;
    }
  }
}
