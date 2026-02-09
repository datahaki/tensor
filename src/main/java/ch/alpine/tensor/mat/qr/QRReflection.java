// code by jph
package ch.alpine.tensor.mat.qr;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.nrm.NormalizeUnlessZero;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.sca.Conjugate;

/** computes dot product {I - TensorProduct[vc, vr]) . tensor
 * followed by negating the k-th row */
/* package */ class QRReflection {
  private static final TensorUnaryOperator NORMALIZE_UNLESS_ZERO = NormalizeUnlessZero.with(Vector2Norm::of);
  // ---
  private final int k;
  /** vc must have the units of vr negated so that the tensor product
   * is a unitless projection matrix */
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
        vr = x.maps(Unprotect::negateUnit);
      } else {
        vc = x;
        vr = x.add(x).maps(Conjugate.FUNCTION).divide(norm2squared);
      }
    } else {
      vc = NORMALIZE_UNLESS_ZERO.apply(x);
      vr = vc.add(vc).maps(Conjugate.FUNCTION);
    }
  }

  /** @param tensor
   * @return */
  public Tensor forward(Tensor tensor) {
    Tensor project = tensor.add(TensorProduct.of(vc.negate(), vr.dot(tensor)));
    project.set(Tensor::negate, k); // 2nd reflection
    return project;
  }
}
