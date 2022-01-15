// code by jph
package ch.alpine.tensor.mat.cd;

import java.util.stream.IntStream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.qty.Quantity;

/** TODO algorithm is only partially suitable for use with {@link Quantity}
 * 
 * Reference:
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

  /** @param matrix symmetric positive definite
   * @param b vector
   * @param x
   * @param n strictly positive
   * @return matrix \ b */
  public static Tensor of(Tensor matrix, Tensor b, Tensor x, int n) {
    n = Integers.requirePositive(Math.min(n, matrix.length()));
    // Tensor r = b.subtract(matrix.dot(x)); // residual
    // Tensor d = r;
    // for (int index = 0; index < n; ++index) {
    // Tensor _d = d.map(ConjugateGradientSolve::negateUnit);
    // Tensor dm = _d.dot(matrix);
    // Tensor _r = r.map(Unprotect::withoutUnit);
    // Scalar r2 = (Scalar) _r.map(Conjugate.FUNCTION).dot(_r);
    // Scalar a = r2.divide((Scalar) dm.dot(_d));
    // x = x.add(_d.multiply(a));
    // Tensor dma = dm.multiply(a);
    // Tensor s = r.subtract(dma);
    // Tensor _s = s.map(Unprotect::withoutUnit);
    // Scalar beta = (Scalar) _s.dot(_s).divide(r2);
    // d = s.add(d.multiply(beta));
    // r = s;
    // if (Tolerance.CHOP.isZero(Vector2Norm.of(r.map(Unprotect::withoutUnit))))
    // break; // if residual r is small could exit
    // }
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
  // public static Scalar negateUnit(Scalar scalar) {
  // if (scalar instanceof Quantity) {
  // Quantity quantity = (Quantity) scalar;
  // return Quantity.of(quantity.value(), quantity.unit().negate());
  // }
  // return scalar;
  // }
}
