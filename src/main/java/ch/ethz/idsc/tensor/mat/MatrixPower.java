// code by jph
package ch.ethz.idsc.tensor.mat;

import java.math.BigInteger;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.num.BinaryPower;
import ch.ethz.idsc.tensor.sca.Power;

/** Implementation is consistent with Mathematica.
 * 
 * For non-square matrix input:
 * <pre>
 * MatrixPower[{{1, 2}}, 0] => Exception
 * MatrixPower[{{1, 2}}, 1] => Exception
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixPower.html">MatrixPower</a> */
public enum MatrixPower {
  ;
  /** @param matrix square
   * @param exponent
   * @return matrix ^ exponent
   * @throws Exception if matrix is not square */
  public static Tensor of(Tensor matrix, long exponent) {
    return of(matrix, BigInteger.valueOf(exponent));
  }

  /** @param matrix square
   * @param exponent
   * @return matrix ^ exponent
   * @throws Exception if matrix is not square */
  public static Tensor of(Tensor matrix, BigInteger exponent) {
    BinaryPower<Tensor> binaryPower = new BinaryPower<>(new MatrixProduct(matrix.length()));
    return binaryPower.raise(SquareMatrixQ.require(matrix), exponent);
  }

  /** @param matrix symmetric
   * @param exponent
   * @return */
  public static Tensor ofSymmetric(Tensor matrix, Scalar exponent) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor vectors = eigensystem.vectors(); // OrthogonalMatrixQ
    return Transpose.of(vectors).dot(eigensystem.values().map(Power.function(exponent)).pmul(vectors));
  }
}
