// code by jph
package ch.alpine.tensor.mat.cd;

import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Conjugate;

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
    return of(matrix, b, x, n);
  }

  /** @param matrix
   * @param b
   * @param x
   * @param n
   * @return */
  public static Tensor of(Tensor matrix, Tensor b, Tensor x, int n) {
    n = Integers.requirePositive(Math.min(n, matrix.length()));
    Tensor r = b.subtract(matrix.dot(x)); // residual
    Tensor d = r;
    for (int index = 0; index < n; ++index) {
      Tensor _d = d.map(ConjugateGradientSolve::negateUnit);
      Tensor dm = _d.dot(matrix);
      Tensor _r = r.map(Unprotect::withoutUnit);
      Scalar r2 = (Scalar) _r.map(Conjugate.FUNCTION).dot(_r);
      Scalar a = r2.divide((Scalar) dm.dot(_d));
      x = x.add(_d.multiply(a));
      Tensor dma = dm.multiply(a);
      Tensor s = r.subtract(dma);
      Tensor _s = s.map(Unprotect::withoutUnit);
      Scalar beta = (Scalar) _s.dot(_s).divide(r2);
      d = s.add(d.multiply(beta));
      r = s;
      if (Tolerance.CHOP.isZero(Vector2Norm.of(r.map(Unprotect::withoutUnit))))
        break; // if residual r is small could exit
    }
    return x;
  }

  /** @param matrix symmetric positive definite
   * @param b
   * @return matrix \ b */
  public static Tensor of(Tensor matrix, Tensor b) {
    return of(matrix, b, matrix.length());
  }

  public static Scalar negateUnit(Scalar scalar) {
    if (scalar instanceof Quantity) {
      Quantity quantity = (Quantity) scalar;
      return Quantity.of(quantity.value(), quantity.unit().negate());
    }
    return scalar;
  }
}
