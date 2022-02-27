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
  public final Scalar det() {
    Tensor r = getR();
    Tensor qct = getQConjugateTranspose();
    if (r.length() == Unprotect.dimension1Hint(r) && // check if R is square
        qct.length() == Unprotect.dimension1Hint(qct)) { // check if RInv is square
      int[] sigma = sigma();
      return IntStream.range(0, sigma.length) //
          .mapToObj(i -> r.Get(i, sigma[i])) //
          .reduce(Scalar::multiply).orElseThrow();
    }
    return RealScalar.ZERO;
  }

  @Override // from Object
  public final String toString() {
    return String.format("%s[%s]", //
        QRDecomposition.class.getSimpleName(), //
        Tensors.message(getQ(), getR()));
  }
}
