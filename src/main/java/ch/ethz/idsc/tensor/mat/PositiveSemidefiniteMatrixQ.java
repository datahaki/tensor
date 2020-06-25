// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/PositiveSemidefiniteMatrixQ.html">PositiveSemidefiniteMatrixQ</a>
 * 
 * @see NegativeSemidefiniteMatrixQ */
public enum PositiveSemidefiniteMatrixQ {
  ;
  /** @param tensor
   * @param chop
   * @return true if tensor is a positive semi-definite matrix
   * @throws TensorRuntimeException if result cannot be established */
  public static boolean ofHermitian(Tensor tensor, Chop chop) {
    return StaticHelper.definite(tensor, chop, Sign::isPositiveOrZero);
  }

  /** @param tensor
   * @return true if tensor is a positive semi-definite matrix
   * @throws TensorRuntimeException if result cannot be established */
  public static boolean ofHermitian(Tensor tensor) {
    return ofHermitian(tensor, Tolerance.CHOP);
  }
}
