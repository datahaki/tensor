// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.lie.MatrixPower;
import ch.ethz.idsc.tensor.red.KroneckerDelta;

/** implementation is consistent with Mathematica.
 * 
 * <pre>
 * IdentityMatrix[2] == {{1, 0}, {0, 1}}
 * </pre>
 * 
 * For non-positive input:
 * <pre>
 * IdentityMatrix[0] => Exception
 * IdentityMatrix[-3] => Exception
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/IdentityMatrix.html">IdentityMatrix</a>
 * 
 * @see DiagonalMatrix
 * @see MatrixPower
 * @see Inverse */
public enum IdentityMatrix {
  ;
  /** @param n positive
   * @return identity matrix of dimensions n x n
   * @throws Exception if n is negative or zero */
  public static Tensor of(int n) {
    Integers.requirePositive(n);
    return Tensors.matrix(KroneckerDelta::of, n, n);
  }

  /** @param matrix square
   * @return
   * @throws Exception if given matrix is not a square matrix */
  public static Tensor of(Tensor matrix) {
    int m = Unprotect.dimension1(matrix);
    if (matrix.length() == m)
      return DiagonalMatrix.of(m, matrix.Get(0, 0).one());
    throw TensorRuntimeException.of(matrix);
  }
}
