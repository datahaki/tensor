// code by jph
package ch.ethz.idsc.tensor.lie;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.nrm.Norm2Bound;
import ch.ethz.idsc.tensor.sca.Chop;

/** Reference: Wikipedia */
public enum BenIsraelCohen {
  ;
  /** @param matrix with rows => cols
   * @param chop
   * @param max
   * @return pseudoinverse of given matrix */
  public static Tensor of(Tensor matrix, Chop chop, int max) {
    Scalar sigma = Norm2Bound.ofMatrix(matrix);
    Tensor a0 = ConjugateTranspose.of(matrix.divide(sigma.multiply(sigma)));
    for (int n = 0; n < max; ++n)
      if (chop.isClose(a0, a0 = refine(matrix, a0)))
        return a0;
    throw TensorRuntimeException.of(matrix);
  }

  public static Tensor refine(Tensor a, Tensor ai) {
    return ai.subtract(Dot.of(ai, a, ai)).add(ai);
  }
}
