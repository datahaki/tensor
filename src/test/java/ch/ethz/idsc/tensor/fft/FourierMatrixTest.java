// code by jph
package ch.ethz.idsc.tensor.fft;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.ConjugateTranspose;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import ch.ethz.idsc.tensor.red.Frobenius;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class FourierMatrixTest extends TestCase {
  public void checkFormat(int n) {
    Tensor zeros = Array.zeros(n, n);
    Tensor original = FourierMatrix.of(n);
    assertTrue(SymmetricMatrixQ.of(original));
    Tensor matrix = Chop._12.of(original);
    assertTrue(SymmetricMatrixQ.of(matrix));
    Tensor invert = ConjugateTranspose.of(matrix);
    assertTrue(SymmetricMatrixQ.of(matrix));
    assertEquals(Chop._12.of(matrix.dot(invert).subtract(IdentityMatrix.of(n))), zeros);
    Chop._12.requireClose(Inverse.of(matrix), invert);
  }

  public void testSeveral() {
    for (int n = 1; n <= 10; ++n)
      checkFormat(n);
    checkFormat(32);
  }

  public void testNorm4() {
    Tensor m = FourierMatrix.of(4);
    assertEquals(Norm._1.ofMatrix(m), RealScalar.of(2));
    assertEquals(Norm._1.ofMatrix(m), Norm.INFINITY.of(m));
    assertEquals(Norm._1.ofMatrix(m), Frobenius.of(m));
    // Norm._2.of m == 1 is confirmed with Mathematica
  }

  private static void _check(int n) {
    Tensor matrix = FourierMatrix.of(n);
    Tensor inverse = FourierMatrix.inverse(n);
    Chop._10.requireClose(matrix.dot(inverse), IdentityMatrix.of(n));
  }

  public void testInverse() {
    _check(8);
    _check(10);
    _check(11);
  }

  public void testNegativeFail() {
    AssertFail.of(() -> FourierMatrix.of(0));
    AssertFail.of(() -> FourierMatrix.of(-1));
  }
}