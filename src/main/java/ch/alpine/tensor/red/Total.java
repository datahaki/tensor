// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.nrm.NormalizeTotal;

/** implementation is consistent with Mathematica.
 * 
 * <p>Mathematica allows
 * Normalize[{-1, 2, 3}, Total] == {-1/4, 1/2, 3/4}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Total.html">Total</a>
 * 
 * @see NormalizeTotal */
public enum Total {
  ;
  /** The return value has {@link Dimensions} of input tensor reduced by 1.
   * 
   * <pre>
   * Total[{a, b, c, d}] == a + b + c + d
   * Total[{}] == 0
   * Total[{{1, 2}, {3, 4}, {5, 6}}] == {9, 12}
   * </pre>
   * 
   * <p>Scalar input is not allowed. For example, Total[3.14] throws an exception.
   * 
   * @param tensor
   * @return total sum of tensor entries at first level, or 0 if tensor is empty
   * @throws Throw if input tensor is a scalar */
  public static Tensor of(Tensor tensor) {
    return tensor.stream().reduce(Tensor::add).orElse(RealScalar.ZERO);
  }

  /** The function {@link #ofVector(Tensor)} is consistent with {@link #of(Tensor)}
   * for vector valued input.
   * 
   * <pre>
   * Total.ofVector[{a, b, c, d}] == a + b + c + d
   * Total.ofVector[{}] == 0
   * </pre>
   * 
   * <p>Scalar input is not allowed. For example, Total[3.14] throws an exception.
   * 
   * @param vector
   * @return total sum of vector entries
   * @throws Throw if input is not a vector */
  public static Scalar ofVector(Tensor vector) {
    return (Scalar) of(vector);
  }
}
