// code by jph
package ch.ethz.idsc.tensor.lie;

import java.math.BigInteger;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Pivot;
import ch.ethz.idsc.tensor.mat.Pivots;
import ch.ethz.idsc.tensor.mat.SquareMatrixQ;
import ch.ethz.idsc.tensor.num.BinaryPower;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.sca.Power;

/** Implementation is consistent with Mathematica.
 * 
 * <p>For non-square matrix input:
 * <pre>
 * MatrixPower[{{1, 2}}, 0] => Exception
 * MatrixPower[{{1, 2}}, 1] => Exception
 * </pre>
 * 
 * <p>Remark: Large exponents typically do not make sense for matrices with entries of
 * real scalar. On the other hand, for matrices with entries of type {@link GaussScalar}
 * the result of large exponents is numerically bounded.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixPower.html">MatrixPower</a> */
public enum MatrixPower {
  ;
  /** @param matrix square
   * @param exponent
   * @return matrix ^ exponent
   * @throws Exception if matrix is not square */
  public static Tensor of(Tensor matrix, BigInteger exponent) {
    return of(matrix, exponent, RealScalar.ONE, Pivots.ARGMAX_ABS);
  }

  /** @param matrix square
   * @param exponent
   * @return matrix ^ exponent
   * @throws Exception if matrix is not square */
  public static Tensor of(Tensor matrix, long exponent) {
    return of(matrix, BigInteger.valueOf(exponent), RealScalar.ONE, Pivots.ARGMAX_ABS);
  }

  /***************************************************/
  /** @param matrix
   * @param exponent
   * @param one
   * @param pivot
   * @return */
  public static Tensor of(Tensor matrix, BigInteger exponent, Scalar one, Pivot pivot) {
    BinaryPower<Tensor> binaryPower = new BinaryPower<>(new MatrixProduct(matrix.length(), one, pivot));
    // check for square matrix is required when exponent in {0, 1}
    return binaryPower.raise(SquareMatrixQ.require(matrix), exponent);
  }

  /** @param matrix
   * @param exponent
   * @param one
   * @return */
  public static Tensor of(Tensor matrix, long exponent, Scalar one, Pivot pivot) {
    return of(matrix, BigInteger.valueOf(exponent), one, pivot);
  }

  /***************************************************/
  /** @param matrix symmetric
   * @param exponent
   * @return */
  public static Tensor ofSymmetric(Tensor matrix, Scalar exponent) {
    return Eigenvalues.ofSymmetric_map(matrix, Power.function(exponent));
  }
}
