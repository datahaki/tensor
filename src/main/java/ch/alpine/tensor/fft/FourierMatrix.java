// code by jph
package ch.alpine.tensor.fft;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.Sqrt;

/** applications of {@link FourierMatrix} is to perform {@link Fourier} transform
 * and inverse transform of vectors or matrices of arbitrary dimensions.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/FourierMatrix.html">FourierMatrix</a> */
public enum FourierMatrix {
  ;
  /** @param n positive
   * @return square matrix of dimensions [n x n] with complex entries
   * <code>(i, j) -> sqrt(1/n) exp(i * j * 2pi/n *I)</code> */
  public static Tensor of(int n) {
    Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, Integers.requirePositive(n)));
    return Tensors.matrix((i, j) -> //
    ComplexScalar.unit(RationalScalar.of(i * j, n).multiply(Pi.TWO)).multiply(scalar), n, n);
  }

  /** @param n
   * @return inverse of fourier matrix */
  public static Tensor inverse(int n) {
    return ConjugateTranspose.of(of(n));
  }
}
