// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.TensorProduct;
import ch.ethz.idsc.tensor.nrm.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.nrm.Vector2NormSquared;
import ch.ethz.idsc.tensor.sca.Conjugate;

/** computes dot product {I - TensorProduct[vc, vr]) . tensor
 * followed by negating the k-th row */
/* package */ class QRReflection {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Vector2Norm::of);
  // ---
  private final int k;
  private final Tensor vc; // column vector
  private final Tensor vr; // row vector

  /** @param k
   * @param x */
  public QRReflection(int k, Tensor x) {
    this.k = k;
    if (ExactTensorQ.of(x)) {
      Scalar norm2squared = Vector2NormSquared.of(x);
      if (Scalars.isZero(norm2squared)) {
        vc = x;
        vr = x;
      } else {
        vc = x;
        vr = Conjugate.of(x.add(x)).divide(norm2squared);
      }
    } else {
      vc = NORMALIZE_UNLESS_ZERO.apply(x);
      vr = Conjugate.of(vc.add(vc));
    }
  }

  public Tensor forward(Tensor tensor) {
    Tensor project = tensor.add(TensorProduct.of(vc.negate(), vr.dot(tensor)));
    project.set(Tensor::negate, k); // 2nd reflection
    return project;
  }
}