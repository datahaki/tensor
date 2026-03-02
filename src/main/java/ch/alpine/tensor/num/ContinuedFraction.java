// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Round;

public enum ContinuedFraction {
  FLOOR(Floor.FUNCTION),
  ROUND(Round.FUNCTION);

  private final ScalarUnaryOperator suo;

  ContinuedFraction(ScalarUnaryOperator suo) {
    this.suo = suo;
  }

  public Tensor of(Scalar x, int maxIterations, Chop chop) {
    Tensor coeffs = Tensors.empty();
    Scalar value = x;
    for (int i = 0; i < maxIterations; ++i) {
      Scalar a = suo.apply(value);
      coeffs.append(a);
      Scalar rem = value.subtract(a);
      if (chop.isZero(N.DOUBLE.apply(rem)))
        break;
      value = rem.reciprocal();
    }
    return coeffs;
  }
}
