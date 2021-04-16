// code by jph
package ch.ethz.idsc.tensor.mat.qr;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;

public abstract class QRDecompositionBase implements QRDecomposition {
  @Override // from QRDecomposition
  public final Tensor getQ() {
    return ConjugateTranspose.of(getInverseQ()); // n x min(n, m)
  }

  @Override
  public final Tensor pseudoInverse() {
    Tensor R = getR();
    Tensor Qinv = getInverseQ();
    int m = Unprotect.dimension1Hint(R);
    StaticHelper.failFast(R, m);
    Tensor[] x = Qinv.stream().limit(m).toArray(Tensor[]::new);
    for (int i = m - 1; i >= 0; --i) {
      for (int j = i + 1; j < m; ++j)
        x[i] = x[i].subtract(x[j].multiply(R.Get(i, j)));
      x[i] = x[i].divide(R.Get(i, i));
    }
    return Unprotect.byRef(x);
  }

  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", //
        QRDecomposition.class.getSimpleName(), //
        Tensors.message(getQ(), getR()));
  }
}
