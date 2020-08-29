// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.sca.Chop;

/** consistent with Mathematica:
 * SymmetricMatrixQ[ {} ] == false
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SymmetricMatrixQ.html">SymmetricMatrixQ</a>
 * 
 * @see HermitianMatrixQ
 * @see UnitaryMatrixQ
 * @see AntisymmetricMatrixQ */
public enum SymmetricMatrixQ {
  ;
  /** @param tensor
   * @param chop
   * @return true if given tensor is a symmetric matrix */
  public static boolean of(Tensor tensor, Chop chop) {
    return StaticHelper.addId(tensor, chop, Transpose::of);
  }

  /** @param tensor
   * @return true if given tensor is a symmetric matrix */
  public static boolean of(Tensor tensor) {
    return of(tensor, Tolerance.CHOP);
  }

  /** @param tensor
   * @param chop
   * @return
   * @throws Exception if given tensor is not a symmetric matrix with given tolerance */
  public static Tensor require(Tensor tensor, Chop chop) {
    if (of(tensor, chop))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }

  /** @param tensor
   * @return
   * @throws Exception if given tensor is not a symmetric matrix */
  public static Tensor require(Tensor tensor) {
    return require(tensor, Tolerance.CHOP);
  }
}
