// code by jph
package ch.alpine.tensor.mat.qr;

import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.mat.ConjugateTranspose;

/* package */ abstract class QRDecompositionBase implements QRDecomposition {
  @Override // from QRDecomposition
  public final Tensor getQ() {
    return ConjugateTranspose.of(getQConjugateTranspose()); // n x min(n, m)
  }

  @Override // from QRDecomposition
  public final Tensor pseudoInverse() {
    return RSolve.of(this, getQConjugateTranspose());
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
}
