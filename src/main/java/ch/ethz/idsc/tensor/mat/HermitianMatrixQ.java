// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Chop;

/** Quote from Wikipedia: A Hermitian matrix (or self-adjoint matrix) is
 * a complex square matrix that is equal to its own conjugate transpose.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HermitianMatrixQ.html">HermitianMatrixQ</a>
 * 
 * @see SymmetricMatrixQ
 * @see AntisymmetricMatrixQ */
public enum HermitianMatrixQ {
  ;
  /** @param tensor
   * @param chop
   * @return true if tensor is a Hermitian matrix */
  public static boolean of(Tensor tensor, Chop chop) {
    return StaticHelper.addId(tensor, chop, ConjugateTranspose::of);
  }

  /** @param tensor
   * @return true if tensor is a Hermitian matrix */
  public static boolean of(Tensor tensor) {
    return of(tensor, Tolerance.CHOP);
  }

  /** @param tensor
   * @param chop
   * @return
   * @throws Exception if given tensor is not a Hermitian matrix with given tolerance */
  public static Tensor require(Tensor tensor, Chop chop) {
    if (of(tensor, chop))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }

  /** @param tensor
   * @return
   * @throws Exception if given tensor is not a Hermitian matrix */
  public static Tensor require(Tensor tensor) {
    return require(tensor, Tolerance.CHOP);
  }
}
