// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.red.KroneckerDelta;

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

  /** function provides the neutral multiplicative element for a matrix
   * with entries of type {@link GaussScalar}, etc.
   * 
   * @param matrix square
   * @return
   * @throws Exception if given matrix is not a square matrix */
  public static Tensor of(Tensor matrix) {
    int n = Integers.requireEquals(matrix.length(), Unprotect.dimension1(matrix));
    return Tensors.matrix((i, j) -> i.equals(j) ? matrix.Get(i, j).one() : matrix.Get(i, j).zero(), n, n);
  }
}
