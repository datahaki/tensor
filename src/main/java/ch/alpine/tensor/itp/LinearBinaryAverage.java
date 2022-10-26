// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** implements binary average as
 * <pre>
 * p * (1 - lambda) + q * lambda == p + lambda * (q - p)
 * </pre>
 * 
 * The implementation guarantees that
 * for lambda == 0 the return value equals to p
 * for lambda == 1 the return value equals to q
 * 
 * Remark:
 * implementation is useful when only few interpolations
 * between p and q are needed.
 * Otherwise, the difference q - p should be pre-computed.
 * 
 * @see LinearInterpolation */
public enum LinearBinaryAverage implements BinaryAverage {
  INSTANCE;

  @Override // from BinaryAverage
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Tensor shift = q.subtract(p).multiply(scalar);
    return scalar.one().equals(scalar) //
        ? q.copy()
        : p.add(shift);
  }
}
