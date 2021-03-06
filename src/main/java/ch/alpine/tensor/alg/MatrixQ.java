// code by jph
package ch.alpine.tensor.alg;

import java.util.Arrays;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;

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
    Dimensions dimensions = new Dimensions(tensor);
    return dimensions.list().equals(Arrays.asList(rows, cols)) //
        && dimensions.isArray();
  }

  /** @param tensor
   * @return given tensor
   * @throws Exception if given tensor is not a matrix */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }

  /** @param tensor
   * @param rows
   * @param cols
   * @return given tensor
   * @throws Exception if given tensor is not a matrix with given number of rows and columns */
  public static Tensor requireSize(Tensor tensor, int rows, int cols) {
    if (ofSize(tensor, rows, cols))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }
}
