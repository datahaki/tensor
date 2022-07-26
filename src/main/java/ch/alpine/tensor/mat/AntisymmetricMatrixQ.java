// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.sca.Chop;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/AntisymmetricMatrixQ.html">AntisymmetricMatrixQ</a>
 * 
 * @see HermitianMatrixQ */
public enum AntisymmetricMatrixQ {
  ;
  /** @param tensor
   * @param chop
   * @return true if tensor is an anti-symmetric matrix */
  public static boolean of(Tensor tensor, Chop chop) {
    return StaticHelper.addId(tensor, chop, matrix -> Transpose.of(matrix).negate());
  }

  /** @param tensor
   * @return true if tensor is an anti-symmetric matrix */
  public static boolean of(Tensor tensor) {
    return of(tensor, Tolerance.CHOP);
  }

  /** @param tensor
   * @param chop
   * @return
   * @throws Exception if given tensor is not a anti-symmetric matrix with given tolerance */
  public static Tensor require(Tensor tensor, Chop chop) {
    if (of(tensor, chop))
      return tensor;
    throw new Throw(tensor);
  }

  /** @param tensor
   * @return
   * @throws Exception if given tensor is not a anti-symmetric matrix */
  public static Tensor require(Tensor tensor) {
    return require(tensor, Tolerance.CHOP);
  }
}
