// code by jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.RationalScalar;
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

  /** @param length
   * @param window
   * @return symmetric vector of given length of weights that sum up to length */
  public static Tensor weights(int length, ScalarUnaryOperator window) {
    Tensor samples = 1 == length //
        ? SINGLE_ZERO
        : samples(length);
    Tensor weights = samples.map(window);
    return weights.multiply(RealScalar.of(length).divide(Total.ofVector(weights)));
  }

  /** @param length
   * @return vector of given length */
  public static Tensor samples(int length) {
    Scalar scalar = RationalScalar.HALF.add(RationalScalar.of(-1, 2 * length));
    return Subdivide.of(scalar.negate(), scalar, length - 1);
  }
}
