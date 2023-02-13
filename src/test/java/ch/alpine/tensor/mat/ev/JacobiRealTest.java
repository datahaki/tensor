// code by jph
package ch.alpine.tensor.mat.ev;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.ev.JacobiReal.GivensReal;
import ch.alpine.tensor.nrm.Hypot;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class JacobiRealTest {
  @Test
  void testRandom() {
    RandomGenerator random = new Random(1);
    Distribution distribution = UniformDistribution.of(-10, 10);
    for (int d = 2; d < 10; ++d)
      for (int count = 0; count < 5; ++count) {
        Tensor matrix = Symmetrize.of(RandomVariate.of(distribution, random, d, d));
        Eigensystem eigensystem = Eigensystem.ofSymmetric(matrix);
        Tensor diagon = DiagonalMatrix.with(eigensystem.values());
        Tensor m1 = BasisTransform.ofMatrix(diagon, eigensystem.vectors());
        Tolerance.CHOP.requireClose(m1, matrix);
      }
  }

  @Test
  void testSimple() {
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

  @Test
  void testOneStep() {
    Tensor matrix = HilbertMatrix.of(4).unmodifiable();
    JacobiReal jacobiReal = new JacobiReal(matrix);
    Scalar[][] A = jacobiReal.H;
    Tensor[] V = jacobiReal.V;
    int n = A.length;
    for (int count = 0; count < 5; ++count)
      for (int p = 0; p < n - 1; ++p)
        for (int q = p + 1; q < n; ++q) {
          _check(matrix, A, V);
          jacobiReal.eliminate(p, q);
          // JacobiRotation.transform(A, V, p, q);
          _check(matrix, A, V);
        }
  }

  // for testing
  public static Tensor rotation(int n, int p, int q, Scalar t) {
    Scalar ci = Hypot.withOne(t);
    Tensor tensor = IdentityMatrix.of(n);
    {
      Scalar c = ci.reciprocal();
      tensor.set(c, p, p);
      tensor.set(c, q, q);
    }
    {
      Scalar s = t.divide(ci);
      tensor.set(s, p, q);
      tensor.set(s.negate(), q, p);
    }
    return tensor;
  }

  @Test
  void testEmulation() {
    Tensor matrix = HilbertMatrix.of(4).unmodifiable();
    int p = 1;
    int q = 2;
    JacobiReal jacobiReal = new JacobiReal(matrix);
    Scalar[][] A = jacobiReal.H;
    Tensor[] V = jacobiReal.V;
    // jacobiReal.run(p, q);
    // JacobiRotation jacobiRotation = new JacobiRotation(A, V, p, q);
    Scalar dif = A[q][q].subtract(A[p][p]);
    Scalar hpq = A[p][q];
    Scalar t = JacobiReal.t(dif, hpq);
    GivensReal givensReal = jacobiReal.new GivensReal(t);
    givensReal.transform(p, q);
    givensReal.dot(p, q);
    Tensor r = rotation(A.length, p, q, t);
    Tolerance.CHOP.requireClose(Unprotect.byRef(V), Transpose.of(r));
    Tolerance.CHOP.requireClose( //
        Tensors.matrix(A), //
        BasisTransform.of(matrix, 1, r));
  }

  @Test
  void testEpsDouble() {
    double dbl_ulp = Math.ulp(1.0);
    float flt_ulp = Math.ulp(1);
    assertTrue(dbl_ulp < flt_ulp);
  }

  @Test
  void testPackage() {
    assertFalse(Modifier.isPublic(JacobiReal.class.getModifiers()));
  }
}
