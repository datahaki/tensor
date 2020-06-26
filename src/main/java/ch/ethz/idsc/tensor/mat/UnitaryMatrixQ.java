// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.sca.Chop;

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
    return StaticHelper.dotId(tensor, chop, ConjugateTranspose::of);
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
    throw TensorRuntimeException.of(tensor);
  }

  /** @param tensor
   * @return
   * @return whether tensor is a matrix and tensor.ConjugateTranspose[tensor] is the identity matrix */
  public static Tensor require(Tensor tensor) {
    return require(tensor, Tolerance.CHOP);
  }
}
