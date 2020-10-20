// code by jph
package ch.ethz.idsc.tensor.itp;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** implements binary average as
 * <pre>
 * p * (1 - lambda) + q * lambda == p + lambda * (q - p)
 * </pre> */
/* package */ enum LinearBinaryAverage implements BinaryAverage {
  INSTANCE;

  @Override // from BinaryAverage
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return q.subtract(p).multiply(scalar).add(p);
  }
}
