// code by jph
package ch.alpine.tensor.mat.ev;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Abs;
import junit.framework.TestCase;

public class JacobiRotationTest extends TestCase {
  private static final Scalar HUNDRED = DoubleScalar.of(100);

  public void testSimple() {
    Tensor matrix = HilbertMatrix.of(4).unmodifiable();
    Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
    Tensor vs = eigensystem.vectors();
    Tensor D = DiagonalMatrix.with(eigensystem.values());
    Tensor v = Transpose.of(vs);
    Tolerance.CHOP.requireClose(Dot.of(v, D, vs), matrix);
    Tolerance.CHOP.requireClose(Dot.of(vs, matrix, v), D);
  }

  private static void _check(Tensor matrix, Scalar[][] A, Tensor V) {
    Tensor a = Tensors.matrix(A);
    SymmetricMatrixQ.require(a);
    Tensor Vt = Transpose.of(V);
    Tolerance.CHOP.requireClose(matrix, Dot.of(Vt, a, V));
  }

  public void testOneStep() {
    Tensor matrix = HilbertMatrix.of(4).unmodifiable();
    Scalar[][] A = ScalarArray.ofMatrix(matrix);
    Tensor V = IdentityMatrix.of(A.length);
    int n = A.length;
    for (int count = 0; count < 5; ++count)
      for (int p = 0; p < n - 1; ++p)
        for (int q = p + 1; q < n; ++q) {
          Scalar apq = A[p][q];
          Scalar Apq = Abs.FUNCTION.apply(apq);
          Scalar g = HUNDRED.multiply(Apq);
          // ---
          _check(matrix, A, V);
          JacobiRotation.one(A, V, p, q, g);
          _check(matrix, A, V);
          {
            // Tensor a = Tensors.matrix(A);
            // System.out.println(Pretty.of(a));
          }
        }
  }
}
