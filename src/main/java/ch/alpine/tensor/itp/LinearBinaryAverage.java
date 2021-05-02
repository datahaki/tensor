// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

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
