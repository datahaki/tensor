// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.mat.ConjugateTranspose;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.VandermondeMatrix;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.nrm.FrobeniusNorm;
import ch.alpine.tensor.nrm.Matrix1Norm;
import ch.alpine.tensor.nrm.MatrixInfinityNorm;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.usr.AssertFail;

public class FourierMatrixTest {
  public void checkFormat(int n) {
    Tensor zeros = Array.zeros(n, n);
    Tensor original = FourierMatrix.of(n);
    SymmetricMatrixQ.require(original);
    Tensor matrix = Tolerance.CHOP.of(original);
    SymmetricMatrixQ.require(matrix);
    Tensor invert = ConjugateTranspose.of(matrix);
    SymmetricMatrixQ.require(matrix);
    assertEquals(Tolerance.CHOP.of(matrix.dot(invert).subtract(IdentityMatrix.of(n))), zeros);
    Tolerance.CHOP.requireClose(Inverse.of(matrix), invert);
  }

  @Test
  public void testSeveral() {
    Random random = new Random();
    int n = 1 + random.nextInt(20);
    checkFormat(n);
  }

  @Test
  public void testNorm4() {
    Tensor m = FourierMatrix.of(4);
    assertEquals(Matrix1Norm.of(m), RealScalar.of(2));
    assertEquals(Matrix1Norm.of(m), MatrixInfinityNorm.of(m));
    assertEquals(Matrix1Norm.of(m), FrobeniusNorm.of(m));
    // Norm._2.of m == 1 is confirmed with Mathematica
  }

  @Test
  public void testVandermonde() {
    for (int n = 1; n < 8; ++n) {
      Tensor vector = Subdivide.of(RealScalar.ZERO, Pi.TWO, n).multiply(ComplexScalar.I).map(Exp.FUNCTION).extract(0, n);
      Scalar scalar = Sqrt.FUNCTION.apply(RationalScalar.of(1, n));
      Tensor matrix = VandermondeMatrix.of(vector).multiply(scalar);
      Tolerance.CHOP.requireClose(FourierMatrix.of(n), matrix);
    }
  }

  private static void _check(int n) {
    Tensor matrix = FourierMatrix.of(n);
    Tensor inverse = FourierMatrix.inverse(n);
    Tolerance.CHOP.requireClose(matrix.dot(inverse), IdentityMatrix.of(n));
  }

  @Test
  public void testInverse() {
    _check(8);
    _check(10);
    _check(11);
  }

  @Test
  public void testNegativeFail() {
    AssertFail.of(() -> FourierMatrix.of(0));
    AssertFail.of(() -> FourierMatrix.of(-1));
  }
}
