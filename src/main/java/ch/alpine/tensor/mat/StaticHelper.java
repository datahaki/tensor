// code by jph
package ch.alpine.tensor.mat;

import java.util.function.Predicate;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.MatrixDotTranspose;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;

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

  // ---
  /** @param matrix
   * @return matrix . ConjugateTranspose(matrix) */
  public static Tensor dotConjugate(Tensor matrix) {
    return MatrixDotTranspose.of(matrix, Conjugate.of(matrix));
  }
}
