// code corresponds to "org.hipparchus.linear.HessenbergTransformer" in Hipparchus project
// adapted by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Sign;

/** Equation of decomposition:
 * <pre>
 * p . h . ConjugateTranspose[p] == m
 * </pre>
 * 
 * Implementation works for matrices consisting of scalars of type {@link Quantity}.
 * however, does not work for complex matrices
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HessenbergDecomposition.html">HessenbergDecomposition</a> */
class HessenbergDecompositionHipp implements HessenbergDecomposition, Serializable {
  private final Tensor pam;
  private final Tensor hmt;

  HessenbergDecompositionHipp(Tensor matrix) {
    final int n = matrix.length();
    Scalar[][] hhv = ScalarArray.ofMatrix(matrix);
    Scalar[] ort = new Scalar[n];
    final int last = n - 1;
    for (int m = 1; m < last; ++m) {
      final int mp = m - 1;
      Tensor vec = Tensor.of(IntStream.range(m, n).mapToObj(i -> hhv[i][mp]));
      Scalar norm = Vector2Norm.of(vec);
      if (Scalars.nonZero(norm)) {
        vec = vec.divide(norm); // normalize
        Scalar piv = vec.Get(0);
        final Scalar g = Scalars.nonZero(Im.FUNCTION.apply(piv)) //
            || Sign.isPositive(piv) //
                ? RealScalar.ONE.negate()
                : RealScalar.ONE;
        final Scalar h = piv.multiply(g).subtract(RealScalar.ONE);
        vec.set(g.negate()::add, 0);
        // Apply Householder similarity transformation
        // H = (I - u*u' / h) * H * (I - u*u' / h)
        // Tensor ve2 = Conjugate.of(vec);
        for (int j = m; j < n; ++j) {
          final int fj = j;
          Tensor v = Tensor.of(IntStream.range(m, n).mapToObj(i -> hhv[i][fj]));
          Tensor w = TensorProduct.of(vec, vec.dot(v).divide(h));
          for (int i = m; i < n; ++i)
            hhv[i][j] = hhv[i][j].add(w.Get(i - m));
        }
        for (int i = 0; i < n; ++i) {
          final int fi = i;
          Tensor v = Tensor.of(IntStream.range(m, n).mapToObj(j -> hhv[fi][j]));
          Tensor w = TensorProduct.of(vec, vec.dot(v).divide(h));
          for (int j = m; j < n; ++j)
            hhv[i][j] = hhv[i][j].add(w.Get(j - m));
        }
        ort[m] = norm.multiply(vec.Get(0));
        hhv[m][mp] = norm.multiply(g);
      }
    }
    hmt = UpperTriangularize.of(Tensors.matrix(hhv), -1);
    Scalar[][] pa = ScalarArray.ofMatrix(IdentityMatrix.of(n));
    for (int m = last - 1; 1 <= m; --m) {
      final int mp = m - 1;
      if (Scalars.nonZero(hhv[m][mp])) {
        Tensor vec = Tensor.of(Stream.concat(Stream.of(ort[m]), IntStream.range(m + 1, n).mapToObj(i -> hhv[i][mp])));
        for (int j = m; j < n; ++j) {
          final int fj = j;
          Tensor res = Tensor.of(IntStream.range(m, n).mapToObj(i -> pa[i][fj]));
          Scalar g = (Scalar) vec.dot(res).divide(ort[m]).divide(hhv[m][mp]);
          Tensor ins = vec.multiply(g).add(res);
          for (int i = m; i < n; ++i)
            pa[i][j] = ins.Get(i - m);
        }
      }
    }
    pam = Tensors.matrix(pa);
  }

  @Override
  public Tensor getUnitary() {
    return pam;
  }

  @Override
  public Tensor getH() {
    return hmt;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("HessenbergDecomposition", getUnitary(), getH());
  }
}
