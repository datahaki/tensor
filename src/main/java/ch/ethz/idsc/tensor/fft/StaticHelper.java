// code by jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;

/* package */ enum StaticHelper {
  ;
  private static final Tensor SINGLE_ZERO = Tensors.vector(0);
  private static final Scalar N_HALF = DoubleScalar.of(-0.5);
  private static final Scalar P_HALF = DoubleScalar.of(+0.5);

  /** @param length
   * @param window
   * @return symmetric vector of given length of weights that sum up to length */
  public static Tensor weights(int length, ScalarUnaryOperator window) {
    Tensor samples = 1 == length //
        ? SINGLE_ZERO
        : Subdivide.of(N_HALF, P_HALF, length - 1);
    Tensor weights = samples.map(window);
    return weights.multiply(RealScalar.of(length).divide(Total.ofVector(weights)));
  }
}
