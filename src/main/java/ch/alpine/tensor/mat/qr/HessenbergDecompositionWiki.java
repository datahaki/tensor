// code by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.nrm.Vector2NormSquared;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Conjugate;

/** Equation of decomposition:
 * <pre>
 * p . h . ConjugateTranspose[p] == m
 * </pre>
 * 
 * Implementation works for matrices consisting of scalars of type {@link Quantity}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HessenbergDecomposition.html">HessenbergDecomposition</a> */
class HessenbergDecompositionWiki implements HessenbergDecomposition, Serializable {
  private Tensor u;
  private Tensor h;

  HessenbergDecompositionWiki(Tensor matrix) {
    int n = matrix.length();
    u = IdentityMatrix.of(n);
    h = matrix;
    for (int m = 1; m < n - 1; ++m) {
      final Tensor fA = h;
      final int mp = m - 1;
      Tensor a1 = Tensor.of(IntStream.range(0, n).mapToObj(i -> fA.Get(i, mp)));
      for (int i = 0; i < m; ++i)
        a1.set(Scalar::zero, i);
      Scalar a11 = a1.Get(m);
      Tensor ne1 = UnitVector.of(n, m).multiply(Vector2Norm.of(a1));
      Tensor w = Scalars.isZero(a11) //
          ? ne1.subtract(a1)
          : ne1.add(a1.multiply(Conjugate.FUNCTION.apply(a11).divide(Abs.FUNCTION.apply(a11))));
      Scalar f = RealScalar.TWO.divide(Vector2NormSquared.of(w));
      Tensor s = TensorProduct.of(w.multiply(f), Conjugate.of(w));
      Tensor V1 = IdentityMatrix.of(n).subtract(s);
      h = Dot.of(V1, h, ConjugateTranspose.of(V1));
      u = V1.dot(u);
    }
  }

  @Override
  public Tensor getUnitary() {
    return ConjugateTranspose.of(u);
  }

  @Override
  public Tensor getH() {
    return h;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("HessenbergDecomposition", getUnitary(), getH());
  }
}
