// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.Chop;

/** Mathematica definition:
 * "A matrix m is unitary if m.ConjugateTranspose[m] is the identity matrix."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/UnitaryMatrixQ.html">UnitaryMatrixQ</a> */
public enum UnitaryMatrixQ {
  ;
  /** @param tensor
   * @param chop precision
   * @return true, if tensor is a matrix and tensor.ConjugateTranspose[tensor] is the identity matrix */
  public static boolean of(Tensor tensor, Chop chop) {
    return MatrixQ.of(tensor) //
        && chop.isClose(MatrixDotConjugateTranspose.of(tensor), IdentityMatrix.of(tensor.length()));
  }

  /** @param tensor
   * @return true, if tensor is a matrix and tensor.ConjugateTranspose[tensor] is the identity matrix */
  public static boolean of(Tensor tensor) {
    return of(tensor, Tolerance.CHOP);
  }

  /** @param tensor
   * @param chop
   * @return
   * @return whether tensor is a matrix and tensor.ConjugateTranspose[tensor] is the identity matrix */
  public static Tensor require(Tensor tensor, Chop chop) {
    if (of(tensor, chop))
      return tensor;
    throw Throw.of(tensor);
  }

  /** @param tensor
   * @return
   * @return whether tensor is a matrix and tensor.ConjugateTranspose[tensor] is the identity matrix */
  public static Tensor require(Tensor tensor) {
    return require(tensor, Tolerance.CHOP);
  }
}
