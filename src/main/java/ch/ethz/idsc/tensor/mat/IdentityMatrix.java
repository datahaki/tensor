// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.concurrent.atomic.AtomicInteger;

import ch.ethz.idsc.tensor.Scalar;
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
    if (matrix.length() == Unprotect.dimension1(matrix)) {
      AtomicInteger i = new AtomicInteger();
      return Tensor.of(matrix.stream().map(row -> {
        int index = i.getAndIncrement();
        AtomicInteger j = new AtomicInteger();
        return Tensor.of(row.stream() //
            .map(Scalar.class::cast) //
            .map(scalar -> j.getAndIncrement() == index //
                ? scalar.one()
                : scalar.zero()));
      }));
    }
    throw TensorRuntimeException.of(matrix);
  }
}
