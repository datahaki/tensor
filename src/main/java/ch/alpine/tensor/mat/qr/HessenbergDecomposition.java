// code corresponds to "org.hipparchus.linear.HessenbergTransformer" in Hipparchus project
// adapted by jph
package ch.alpine.tensor.mat.qr;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.UpperTriangularize;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;

/** Equation of decomposition:
 * <pre>
 * p . h . ConjugateTranspose[p] == m
 * </pre>
 * 
 * Implementation works for matrices consisting of scalars of type {@link Quantity}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/HessenbergDecomposition.html">HessenbergDecomposition</a> */
public class HessenbergDecomposition implements Serializable {
  /** @param matrix
   * @return */
  public static HessenbergDecomposition of(Tensor matrix) {
    return new HessenbergDecomposition(SquareMatrixQ.require(matrix));
  }

  // ---
  private final Tensor pam;
  private final Tensor hmt;

  private HessenbergDecomposition(Tensor matrix) {
    final int n = matrix.length();
    Scalar[][] hhv = ScalarArray.ofMatrix(matrix);
    Scalar[] ort = new Scalar[n];
    // ---
    final int last = n - 1;
    for (int m = 1; m < last; ++m) {
      Scalar scale = hhv[m][m - 1].zero(); // Scale column.
      for (int i = m; i < n; ++i)
        scale = scale.add(Abs.FUNCTION.apply(hhv[i][m - 1]));
      if (Scalars.nonZero(scale)) {
        Scalar h = RealScalar.ZERO; // Compute Householder transformation.
        for (int i = last; i >= m; --i) {
          ort[i] = hhv[i][m - 1].divide(scale);
          h = h.add(ort[i].multiply(ort[i]));
        }
        Scalar sqrt = Sqrt.FUNCTION.apply(h);
        final Scalar g = Sign.isPositive(ort[m]) //
            ? sqrt.negate()
            : sqrt;
        h = h.subtract(ort[m].multiply(g));
        ort[m] = ort[m].subtract(g);
        // Apply Householder similarity transformation
        // H = (I - u*u' / h) * H * (I - u*u' / h)
        for (int j = m; j < n; ++j) {
          Scalar f = hhv[last][j].zero();
          for (int i = last; i >= m; --i)
            f = f.add(ort[i].multiply(hhv[i][j]));
          f = f.divide(h);
          for (int i = m; i < n; ++i)
            hhv[i][j] = hhv[i][j].subtract(f.multiply(ort[i]));
        }
        for (int i = 0; i < n; ++i) {
          Scalar f = hhv[i][last].zero();
          for (int j = last; j >= m; --j)
            f = f.add(ort[j].multiply(hhv[i][j]));
          f = f.divide(h);
          for (int j = m; j < n; ++j)
            hhv[i][j] = hhv[i][j].subtract(f.multiply(ort[j]));
        }
        ort[m] = scale.multiply(ort[m]);
        hhv[m][m - 1] = scale.multiply(g);
      }
    }
    hmt = UpperTriangularize.of(Tensors.matrix(hhv), -1);
    Scalar[][] pa = ScalarArray.ofMatrix(IdentityMatrix.of(n));
    for (int m = last - 1; 1 <= m; --m)
      if (Scalars.nonZero(hhv[m][m - 1])) {
        for (int i = m + 1; i < n; ++i)
          ort[i] = hhv[i][m - 1];
        for (int j = m; j < n; ++j) {
          Scalar g = ort[m].zero();
          for (int i = m; i < n; ++i)
            g = g.add(ort[i].multiply(pa[i][j]));
          g = g.divide(ort[m]).divide(hhv[m][m - 1]);
          for (int i = m; i < n; ++i)
            pa[i][j] = pa[i][j].add(g.multiply(ort[i]));
        }
      }
    pam = Tensors.matrix(pa);
  }

  public Tensor getP() {
    return pam;
  }

  public Tensor getH() {
    return hmt;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("HessenbergDecomposition", pam, hmt);
  }
}
