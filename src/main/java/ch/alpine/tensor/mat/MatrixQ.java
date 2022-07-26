// code by jph
package ch.alpine.tensor.mat;

import java.util.List;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.ArrayQ;
import ch.alpine.tensor.alg.Dimensions;

/** The implementation is consistent with Mathematica::MatrixQ, in particular
 * MatrixQ[ {} ] == false
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/MatrixQ.html">MatrixQ</a>
 * 
 * @see HermitianMatrixQ
 * @see PositiveDefiniteMatrixQ */
public enum MatrixQ {
  ;
  /** @param tensor
   * @return true if tensor is a matrix */
  public static boolean of(Tensor tensor) {
    return ArrayQ.ofRank(tensor, 2);
  }

  /** @param tensor
   * @param rows
   * @param cols
   * @return true if tensor is a matrix with given number of rows and columns */
  public static boolean ofSize(Tensor tensor, int rows, int cols) {
    return new Dimensions(tensor).isArrayWith(List.of(rows, cols)::equals);
  }

  /** @param tensor
   * @return given tensor
   * @throws Exception if given tensor is not a matrix */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    throw new Throw(tensor);
  }

  /** @param tensor
   * @param rows
   * @param cols
   * @return given tensor
   * @throws Exception if given tensor is not a matrix with given number of rows and columns */
  public static Tensor requireSize(Tensor tensor, int rows, int cols) {
    if (ofSize(tensor, rows, cols))
      return tensor;
    throw new Throw(tensor);
  }
}
