// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.alg.Dimensions;

/** consistent with Mathematica, in particular SquareMatrixQ[{}] == false
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/SquareMatrixQ.html">SquareMatrixQ</a> */
public enum SquareMatrixQ {
  ;
  /** @param tensor
   * @return true if tensor is a square matrix, otherwise false */
  public static boolean of(Tensor tensor) {
    return new Dimensions(tensor).isArrayWith(list -> list.size() == 2 && list.get(0).equals(list.get(1)));
  }

  /** @param tensor
   * @return given tensor
   * @throws Exception if given tensor is not a square matrix */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }
}
