// code by jph
package ch.alpine.tensor.mat.cd;

import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;

/** Reference:
 * Chapter "Conjugate Gradients for Sx=b"
 * in "Linear Algebra and Learning from Data", p. 121
 * by Gilbert Strang, 2019 */
public enum ConjugateGradientSolve {
  ;
  /** @param matrix symmetric positive definite
   * @param b vector
   * @param n strictly positive
   * @return matrix \ b */
  public static Tensor of(Tensor matrix, Tensor b, int n) {
    n = Integers.requirePositive(Math.min(n, matrix.length()));
    Tensor x = Tensor.of(IntStream.range(0, n) //
        .mapToObj(index -> b.Get(index).divide(matrix.Get(index, index)).zero()));
    Tensor r = b;
    Tensor d = r;
    for (int index = 0; index < n; ++index) {
      Scalar a = (Scalar) r.dot(r).divide((Scalar) matrix.dot(d).dot(d));
      x = x.add(d.multiply(a));
      Tensor s = r.subtract(matrix.dot(d).multiply(a));
      Scalar beta = (Scalar) s.dot(s).divide((Scalar) r.dot(r));
      d = s.add(d.multiply(beta));
      r = s;
    }
    return x;
  }

  /** @param matrix symmetric positive definite
   * @param b
   * @return matrix \ b */
  public static Tensor of(Tensor matrix, Tensor b) {
    return of(matrix, b, matrix.length());
  }
}
