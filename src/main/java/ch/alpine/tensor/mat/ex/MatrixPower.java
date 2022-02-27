// code by jph
package ch.alpine.tensor.mat.ex;

import java.math.BigInteger;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.num.BinaryPower;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.Power;

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
  private static final BinaryPower<Tensor> BINARY_POWER = new BinaryPower<>(MatrixProduct.INSTANCE);

  /** @param matrix square
   * @param exponent
   * @return matrix ^ exponent
   * @throws Exception if matrix is not square */
  public static Tensor of(Tensor matrix, BigInteger exponent) {
    return BINARY_POWER.raise(matrix, exponent);
  }

  /** @param matrix square
   * @param exponent
   * @return matrix ^ exponent
   * @throws Exception if matrix is not square */
  public static Tensor of(Tensor matrix, long exponent) {
    return of(matrix, BigInteger.valueOf(exponent));
  }

  // ---
  /** "More generally, we can define any power of an SPD matrix by taking
   * the power of its eigenvalues or using the formula [...]"
   * 
   * Reference:
   * "Riemannian Geometric Statistics in Medical Image Analysis", 2020
   * Edited by Xavier Pennec, Sommer, P. Thomas Fletcher, p. 80
   * 
   * @param matrix symmetric
   * @param exponent
   * @return */
  public static Tensor ofSymmetric(Tensor matrix, Scalar exponent) {
    return StaticHelper.mapEv(Eigensystem.ofSymmetric(matrix, Tolerance.CHOP), Power.function(exponent));
  }

  /** @param matrix
   * @param exponent
   * @return */
  public static Tensor ofHermitian(Tensor matrix, Scalar exponent) {
    return StaticHelper.mapEv(Eigensystem.ofHermitian(matrix, Tolerance.CHOP), Power.function(exponent));
  }
}
