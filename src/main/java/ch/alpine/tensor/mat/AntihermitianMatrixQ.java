// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.Chop;

/** A is anti-hermitian if A = -ConjugateTranspose[A]
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/AntihermitianMatrixQ.html">AntihermitianMatrixQ</a>
 * 
 * https://en.wikipedia.org/wiki/Skew-Hermitian_matrix */
public enum AntihermitianMatrixQ {
  ;
  /** @param tensor
   * @param chop
   * @return true if tensor is an anti-hermitian matrix */
  public static boolean of(Tensor tensor, Chop chop) {
    return StaticHelper.addId(tensor, chop, matrix -> ConjugateTranspose.of(matrix).negate());
  }

  /** @param tensor
   * @return true if tensor is an anti-hermitian matrix */
  public static boolean of(Tensor tensor) {
    return of(tensor, Tolerance.CHOP);
  }

  /** @param tensor
   * @param chop
   * @return
   * @throws Exception if given tensor is not a anti-hermitian matrix with given tolerance */
  public static Tensor require(Tensor tensor, Chop chop) {
    if (of(tensor, chop))
      return tensor;
    throw new Throw(tensor, chop);
  }

  /** @param tensor
   * @return
   * @throws Exception if given tensor is not a anti-hermitian matrix */
  public static Tensor require(Tensor tensor) {
    return require(tensor, Tolerance.CHOP);
  }
}
