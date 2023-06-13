// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.sca.Chop;

/** Quote:
 * A matrix m is normal if m.ConjugateTranspose[m] == ConjugateTranspose[m].m
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/NormalMatrixQ.html">NormalMatrixQ</a> */
public enum NormalMatrixQ {
  ;
  /** @param tensor
   * @param chop
   * @return true if tensor is an explicitly normal matrix */
  public static boolean of(Tensor tensor, Chop chop) {
    if (SquareMatrixQ.of(tensor)) {
      Tensor ct = ConjugateTranspose.of(tensor);
      return chop.isClose(tensor.dot(ct), ct.dot(tensor));
    }
    return false;
  }

  /** @param tensor
   * @return true if tensor is an explicitly normal matrix */
  public static boolean of(Tensor tensor) {
    return of(tensor, Tolerance.CHOP);
  }

  /** @param tensor
   * @param chop
   * @return
   * @throws Exception if given tensor is not an explicitly normal matrix with given tolerance */
  public static Tensor require(Tensor tensor, Chop chop) {
    if (of(tensor, chop))
      return tensor;
    throw new Throw(tensor, chop);
  }

  /** @param tensor
   * @return
   * @throws Exception if given tensor is not an explicitly normal matrix */
  public static Tensor require(Tensor tensor) {
    return require(tensor, Tolerance.CHOP);
  }
}
