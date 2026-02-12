// code corresponds to "org.hipparchus.linear.HessenbergTransformer" in Hipparchus project
// adapted by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.qty.Quantity;

/** Equation of decomposition:
 * <pre>
 * p . h . ConjugateTranspose[p] == m
 * </pre>
 * 
 * Implementation works for matrices consisting of scalars of type {@link Quantity}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HessenbergDecomposition.html">HessenbergDecomposition</a> */
public interface HessenbergDecomposition {
  /** @param matrix
   * @return */
  static HessenbergDecomposition of(Tensor matrix) {
    return new HessenbergDecompositionImpl(SquareMatrixQ.INSTANCE.require(matrix));
  }

  /** @return */
  Tensor getUnitary();

  /** @return */
  Tensor getH();
}
