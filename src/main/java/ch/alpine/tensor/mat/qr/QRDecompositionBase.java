// code by jph
package ch.alpine.tensor.mat.qr;

import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Abs;

/* package */ abstract class QRDecompositionBase implements QRDecomposition {
  @Override // from QRDecomposition
  public final Tensor getQ() {
    return ConjugateTranspose.of(getQConjugateTranspose()); // n x min(n, m)
  }

  @Override // from QRDecomposition
  public final Tensor pseudoInverse() {
    Tensor r = getR();
    // System.out.println(Pretty.of(r.map(Round._3)));
    int m = Unprotect.dimension1Hint(r);
    int[] sigma = sigma();
    failFast(r, m, sigma);
    Tensor[] x = getQConjugateTranspose().stream().limit(m).toArray(Tensor[]::new);
    for (int i = m - 1; i >= 0; --i) {
      // int si = sigma[i];
      for (int j = i + 1; j < m; ++j)
        x[i] = x[i].subtract(x[j].multiply(r.Get(i, sigma[j])));
      x[i] = x[i].divide(r.Get(i, sigma[i]));
      // System.out.println(r.Get(i, sigma[i]));
    }
    return Unprotect.byRef(x);
  }

  @Override // from QRDecomposition
  public final Scalar det() { // only exact up to sign
    Tensor r = getR();
    int m = Unprotect.dimension1Hint(r);
    int[] sigma = sigma();
    return r.length() == m // check if R is square
        ? IntStream.range(0, sigma.length) //
            .mapToObj(i -> r.Get(i, sigma[i])) //
            .reduce(Scalar::multiply).orElseThrow()
        : RealScalar.ZERO;
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
  private static void failFast(Tensor r, int m, int[] sigma) {
    Tensor diag = Tensors.vector(i -> Abs.FUNCTION.apply(r.Get(i, sigma[i])), m);
    Scalar max = (Scalar) diag.stream().reduce(Max::of).orElseThrow();
    Scalar min = (Scalar) diag.stream().reduce(Min::of).orElseThrow();
    if (Tolerance.CHOP.isZero(min) || //
        Tolerance.CHOP.isZero(min.divide(max)))
      throw TensorRuntimeException.of(max, min);
  }
}
