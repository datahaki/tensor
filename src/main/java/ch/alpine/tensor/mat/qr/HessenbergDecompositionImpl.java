// code by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.UpperTriangularize;
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
 * Implementation works for matrices consisting of scalars of type {@link Quantity}, as well as
 * complex scalars.
 * 
 * Reference:
 * https://en.wikipedia.org/wiki/Hessenberg_matrix
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HessenbergDecomposition.html">HessenbergDecomposition</a> */
/* package */
class HessenbergDecompositionImpl implements HessenbergDecomposition, Serializable {
  private final Tensor u;
  private Tensor h;

  public HessenbergDecompositionImpl(Tensor matrix) {
    int n = matrix.length();
    Tensor u = IdentityMatrix.of(n);
    h = matrix;
    for (int m = 1; m < n - 1; ++m) {
      final int mp = m - 1;
      Tensor a = Tensor.of(IntStream.range(0, n).mapToObj(i -> h.Get(i, mp)));
      for (int i = 0; i < m; ++i)
        a.set(Scalar::zero, i);
      Scalar norm = Vector2Norm.of(a);
      if (Scalars.nonZero(norm)) {
        Scalar piv = a.Get(m);
        Tensor w = Scalars.isZero(piv) //
            ? a.negate()
            : a.multiply(Conjugate.FUNCTION.apply(piv).divide(Abs.FUNCTION.apply(piv)));
        w.set(norm::add, m);
        Scalar f = RealScalar.TWO.divide(Vector2NormSquared.of(w)).negate();
        Tensor cwf = w.multiply(f).map(Conjugate.FUNCTION);
        h = h.add(TensorProduct.of(w, cwf.dot(h)));
        h = h.add(TensorProduct.of(h.dot(w), cwf));
        u = u.add(TensorProduct.of(u.dot(w), cwf));
      }
    }
    this.u = u;
  }

  @Override // from HessenbergDecomposition
  public Tensor getUnitary() {
    return u;
  }

  @Override // from HessenbergDecomposition
  public Tensor getH() {
    return UpperTriangularize.of(h, -1);
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("HessenbergDecomposition", getUnitary(), getH());
  }
}
