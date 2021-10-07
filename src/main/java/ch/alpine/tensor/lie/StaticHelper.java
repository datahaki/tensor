// code by jph
package ch.alpine.tensor.lie;

import java.util.function.Function;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.ev.Eigensystem;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Log;

/** Hint: implementation makes use of eigenvalue decomposition of
 * real-valued symmetric matrices for various applications.
 * 
 * @see MatrixExp
 * @see MatrixLog
 * @see MatrixPower */
/* package */ enum StaticHelper {
  ;
  public static final Function<Integer, Tensor> IDENTITY_MATRIX = Cache.of(IdentityMatrix::of, 16);
  // ---
  private static final ScalarUnaryOperator LOG2 = Log.base(2);

  /** @param norm
   * @return power of 2 */
  public static long exponent(Scalar norm) {
    return 1 << Ceiling.longValueExact(LOG2.apply(norm.add(norm.one())));
  }

  /** @param matrix symmetric
   * @param scalarUnaryOperator applied to eigenvalues
   * @return resulting matrix is basis of given matrix
   * @throws Exception if input is not a real symmetric matrix */
  public static Tensor evMap_ofSymmetric(Tensor matrix, ScalarUnaryOperator scalarUnaryOperator) {
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor values = eigensystem.values().map(scalarUnaryOperator);
    Tensor vectors = eigensystem.vectors();
    return Transpose.of(vectors).dot(values.pmul(vectors));
  }
}
