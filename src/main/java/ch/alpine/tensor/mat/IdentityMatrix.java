// code by jph
package ch.alpine.tensor.mat;

import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.ex.MatrixPower;
import ch.alpine.tensor.mat.re.Inverse;
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

  /** @param matrix square
   * @return
   * @throws Exception if given matrix is not a square matrix */
  public static Tensor of(Tensor matrix) {
    Integers.requireEquals(matrix.length(), Unprotect.dimension1(matrix));
    AtomicInteger atomic_i = new AtomicInteger();
    return Tensor.of(matrix.stream().map(row -> {
      int index = atomic_i.getAndIncrement();
      AtomicInteger atomic_j = new AtomicInteger();
      return Tensor.of(row.stream() //
          .map(Scalar.class::cast) //
          .map(scalar -> atomic_j.getAndIncrement() == index //
              ? scalar.one()
              : scalar.zero()));
    }));
  }
}
