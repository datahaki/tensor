// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ abstract class QRDecompositionBase implements QRDecomposition {
  @Override // from QRDecomposition
  public final Tensor getQ() {
    return ConjugateTranspose.of(getQTranspose()); // n x min(n, m)
  }

  @Override // from QRDecomposition
  public final Tensor pseudoInverse() {
    Tensor r = getR();
    int m = Unprotect.dimension1Hint(r);
    failFast(r, m);
    Tensor[] x = getQTranspose().stream().limit(m).toArray(Tensor[]::new);
    for (int i = m - 1; i >= 0; --i) {
      for (int j = i + 1; j < m; ++j)
        x[i] = x[i].subtract(x[j].multiply(r.Get(i, j)));
      x[i] = x[i].divide(r.Get(i, i));
    }
    return Unprotect.byRef(x);
  }

  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", //
        QRDecomposition.class.getSimpleName(), //
        Tensors.message(getQ(), getR()));
  }

  /** @param r
   * @param m
   * @throws Exception if any diagonal element is below 1E-12, or the ratio
   * between min and max is below that threshold */
  private static void failFast(Tensor r, int m) {
    Tensor diag = Tensors.vector(i -> Abs.FUNCTION.apply(r.Get(i, i)), m);
    Scalar max = (Scalar) diag.stream().reduce(Max::of).get();
    Scalar min = (Scalar) diag.stream().reduce(Min::of).get();
    if (Tolerance.CHOP.isZero(min) || //
        Tolerance.CHOP.isZero(min.divide(max)))
      throw TensorRuntimeException.of(max, min);
  }
}
