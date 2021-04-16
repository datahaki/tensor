// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ enum StaticHelper {
  ;
  /** @param R
   * @param m
   * @throws Exception if any diagonal element is below 1E-12, or the ratio
   * between min and max is below that threshold */
  public static void failFast(Tensor R, int m) {
    Tensor diag = Tensors.vector(i -> Abs.FUNCTION.apply(R.Get(i, i)), m);
    Scalar max = (Scalar) diag.stream().reduce(Max::of).get();
    Scalar min = (Scalar) diag.stream().reduce(Min::of).get();
    if (Tolerance.CHOP.isZero(min) || //
        Tolerance.CHOP.isZero(min.divide(max)))
      throw TensorRuntimeException.of(max, min);
  }
}
