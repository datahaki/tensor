// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.JacobiRotation.Inner;
import junit.framework.TestCase;

public class JacobiRotationTest extends TestCase {
  public void testSimple() {
    Tensor matrix = HilbertMatrix.of(4).unmodifiable();
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor vs = eigensystem.vectors();
    Tensor D = DiagonalMatrix.with(eigensystem.values());
    Tensor v = Transpose.of(vs);
    Tolerance.CHOP.requireClose(Dot.of(v, D, vs), matrix);
    Tolerance.CHOP.requireClose(Dot.of(vs, matrix, v), D);
  }

  private static void _check(Tensor matrix, Scalar[][] A, Tensor[] Vs) {
    Tensor V = Unprotect.byRef(Vs);
    Tensor a = Tensors.matrix(A);
    SymmetricMatrixQ.require(a);
    Tensor Vt = Transpose.of(V);
    Tolerance.CHOP.requireClose(matrix, Dot.of(Vt, a, V));
    Tolerance.CHOP.requireClose(matrix, BasisTransform.ofMatrix(a, V));
  }

  public void testOneStep() {
    Tensor matrix = HilbertMatrix.of(4).unmodifiable();
    Scalar[][] A = ScalarArray.ofMatrix(matrix);
    Tensor[] V = IdentityMatrix.of(A.length).stream().toArray(Tensor[]::new);
    int n = A.length;
    for (int count = 0; count < 5; ++count)
      for (int p = 0; p < n - 1; ++p)
        for (int q = p + 1; q < n; ++q) {
          _check(matrix, A, V);
          JacobiRotation.transform(A, V, p, q);
          _check(matrix, A, V);
        }
  }

  public void testEmulation() {
    Tensor matrix = HilbertMatrix.of(4).unmodifiable();
    Scalar[][] A = ScalarArray.ofMatrix(matrix);
    Tensor[] V = IdentityMatrix.of(A.length).stream().toArray(Tensor[]::new);
    int p = 1;
    int q = 2;
    JacobiRotation jacobiRotation = new JacobiRotation(A, V, p, q);
    Scalar dif = A[q][q].subtract(A[p][p]);
    Scalar hpq = A[p][q];
    Scalar t = JacobiRotation.t(dif, hpq);
    Inner inner = jacobiRotation.new Inner(t);
    inner.transform();
    Tensor r = inner.rotation();
    Tolerance.CHOP.requireClose(Unprotect.byRef(V), Transpose.of(r));
    Tolerance.CHOP.requireClose( //
        Tensors.matrix(A), //
        BasisTransform.of(matrix, 1, r));
  }

  public void testEpsDouble() {
    double dbl_ulp = Math.ulp(1.0);
    float flt_ulp = Math.ulp(1);
    assertTrue(dbl_ulp < flt_ulp);
  }
}
