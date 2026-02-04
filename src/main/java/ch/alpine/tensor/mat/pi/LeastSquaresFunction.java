// code by jph
// https://stats.stackexchange.com/questions/66088/analysis-with-complex-data-anything-different
package ch.alpine.tensor.mat.pi;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.cd.CholeskyDecomposition;
import ch.alpine.tensor.mat.qr.QRDecomposition;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;

/** least squares solution x that approximates
 * <pre>
 * matrix . x ~ b
 * </pre>
 * 
 * The general solution is given by
 * <pre>
 * x == PseudoInverse[m] . b
 * </pre>
 * 
 * However, the computation of the pseudo-inverse can often be avoided.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/LeastSquares.html">LeastSquares</a>
 * 
 * @see CholeskyDecomposition
 * @see QRDecomposition
 * @see SingularValueDecomposition
 * @see PseudoInverse */
// TODO TENSOR
enum LeastSquaresFunction {
  ;
  /** @param matrix
   * @return */
  public static TensorUnaryOperator of(Tensor matrix) {
    return b -> LeastSquares.of(matrix, b);
  }
}
