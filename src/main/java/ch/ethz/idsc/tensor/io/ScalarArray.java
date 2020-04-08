// code by jph
package ch.ethz.idsc.tensor.io;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum ScalarArray {
  ;
  /** Hint: convert back with {@link Tensors#of(Scalar...)}
   * 
   * @param vector
   * @return
   * @throws Exception if given vector is not a tensor of rank 1 */
  public static Scalar[] ofVector(Tensor vector) {
    return vector.stream().map(Scalar.class::cast).toArray(Scalar[]::new);
  }

  /** Hint: convert back with {@link Tensors#matrix(Scalar[][])}
   * 
   * @param matrix not necessarily with array structure
   * @return
   * @throws Exception if given matrix is not a list of vectors */
  public static Scalar[][] ofMatrix(Tensor matrix) {
    return matrix.stream().map(ScalarArray::ofVector).toArray(Scalar[][]::new);
  }
}
