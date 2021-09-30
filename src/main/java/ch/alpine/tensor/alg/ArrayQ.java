// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;

/** ArrayQ is <em>not</em> consistent with Mathematica for scalar input:
 * <pre>
 * Mathematica::ArrayQ[3] == False
 * Tensor::ArrayQ[Scalar] == True
 * </pre>
 * 
 * <p>The tensor library considers {@link Scalar}s to be tensors with
 * regular (=array) structure of rank 0.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ArrayQ.html">ArrayQ</a> */
public enum ArrayQ {
  ;
  /** Examples:
   * <code>
   * ArrayQ[17] == True
   * ArrayQ[{{1, 2, 1}, {3, 4, 7}}] == True
   * ArrayQ[{1, 2, {3}, 4}] == False
   * </code>
   * 
   * @return true if tensor structure is identical at all levels, else false.
   * true for {@link Scalar}s */
  public static boolean of(Tensor tensor) {
    return new Dimensions(tensor).isArray();
  }

  /** @param tensor
   * @param rank
   * @return true, if tensor is an array of given rank
   * @see TensorRank
   * @see VectorQ
   * @see MatrixQ */
  public static boolean ofRank(Tensor tensor, int rank) {
    Dimensions dimensions = new Dimensions(tensor);
    return dimensions.isArray() //
        && dimensions.list().size() == rank;
  }

  /** @param tensor
   * @throws Exception if given tensor does not have array structure */
  public static Tensor require(Tensor tensor) {
    if (of(tensor))
      return tensor;
    throw TensorRuntimeException.of(tensor);
  }
}
