// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.function.Predicate;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;

/** helper functions used in {@link SingularValueDecompositionImpl} */
/* package */ enum StaticHelper {
  ;
  /** predicate checks if tensor
   * 1) is a square matrix, then
   * 2) chop.close(tensor, f(tensor))
   * 
   * @param tensor
   * @param tensorUnaryOperator
   * @return
   * @see SquareMatrixQ */
  public static boolean addId(Tensor tensor, Chop chop, TensorUnaryOperator tensorUnaryOperator) {
    return SquareMatrixQ.of(tensor) //
        && chop.isClose(tensor, tensorUnaryOperator.apply(tensor));
  }

  /** predicate checks a matrix A for A . f(A) == Id
   * 
   * @param tensor
   * @param chop
   * @param tensorUnaryOperator
   * @return */
  public static boolean dotId(Tensor tensor, Chop chop, TensorUnaryOperator tensorUnaryOperator) {
    return MatrixQ.of(tensor) //
        && chop.isClose(tensor.dot(tensorUnaryOperator.apply(tensor)), IdentityMatrix.of(tensor.length()));
  }

  /** @param tensor
   * @param chop
   * @param predicate
   * @return */
  public static boolean definite(Tensor tensor, Chop chop, Predicate<Scalar> predicate) {
    return SquareMatrixQ.of(tensor) //
        && CholeskyDecomposition.of(tensor).diagonal().stream() //
            .map(Scalar.class::cast) //
            .map(chop) //
            .allMatch(predicate);
  }
}
