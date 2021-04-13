// code by jph
package ch.ethz.idsc.tensor.lie;

import java.util.Random;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.TrapezoidalDistribution;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class MatrixSqrtTest extends TestCase {
  private static void _check(Tensor g, MatrixSqrt matrixSqrt) {
    Chop._08.requireClose(matrixSqrt.sqrt().dot(matrixSqrt.sqrt_inverse()), IdentityMatrix.of(g.length()));
    Chop._04.requireClose(matrixSqrt.sqrt().dot(matrixSqrt.sqrt()), g);
    Chop._03.requireClose(matrixSqrt.sqrt_inverse().dot(matrixSqrt.sqrt_inverse()), Inverse.of(g));
  }

  public void testSimple() {
    Tensor a = Tensors.fromString("{{4, 10}, {0, 9}}");
    MatrixSqrt matrixSqrt = MatrixSqrt.of(a);
    _check(a, matrixSqrt);
    Tolerance.CHOP.requireClose(matrixSqrt.sqrt(), Tensors.fromString("{{2, 2}, {0, 3}}"));
  }

  public void testIdentity() {
    for (int n = 1; n <= 5; ++n) {
      Tensor x = IdentityMatrix.of(n);
      _check(x, MatrixSqrt.of(x));
      _check(x, MatrixSqrt.ofSymmetric(x));
    }
  }

  public void testTrapezoidalNormal() {
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    for (int n = 1; n < 7; ++n) {
      Tensor x = RandomVariate.of(distribution, n, n);
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.of(x2));
    }
  }

  public void testRandomDiscreteUniform() {
    Random random = new Random(1);
    for (int n = 1; n < 10; ++n) {
      Tensor x = RandomVariate.of(DiscreteUniformDistribution.of(-200, 200), random, n, n);
      Tensor x2 = x.dot(x);
      MatrixSqrt sqrt = new DenmanBeaversDet(x2, Tolerance.CHOP);
      _check(x2, sqrt);
    }
  }

  public void testRandomSymmetric() {
    Random random = new Random(1);
    for (int n = 1; n < 5; ++n) {
      Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.of(0, 0.2), random, n, n));
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.of(x2));
      _check(x2, MatrixSqrt.ofSymmetric(x2));
    }
  }

  public void testRandomSymmetricQuantity() {
    for (int n = 1; n < 5; ++n) {
      Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.of(0, 0.2), n, n)).map(s -> Quantity.of(s, "m"));
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.ofSymmetric(x2));
    }
  }

  public void testSymNegativeDiagonal() {
    Tensor matrix = DiagonalMatrix.of(-1, -2, -3);
    _check(matrix, MatrixSqrt.of(matrix));
    _check(matrix, MatrixSqrt.ofSymmetric(matrix));
  }

  public void testNonSquareFail() {
    AssertFail.of(() -> MatrixSqrt.of(RandomVariate.of(UniformDistribution.of(-2, 2), 2, 3)));
  }

  public void testNonSymmetricFail() {
    AssertFail.of(() -> MatrixSqrt.ofSymmetric(RandomVariate.of(UniformDistribution.of(-2, 2), 4, 4)));
  }
}
