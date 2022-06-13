// code by jph
package ch.alpine.tensor.mat.ex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TrapezoidalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class MatrixSqrtTest {
  private static void _check(Tensor g, MatrixSqrt matrixSqrt) {
    Chop._08.requireClose(matrixSqrt.sqrt().dot(matrixSqrt.sqrt_inverse()), IdentityMatrix.of(g.length()));
    Chop._04.requireClose(matrixSqrt.sqrt().dot(matrixSqrt.sqrt()), g);
    Chop._03.requireClose(matrixSqrt.sqrt_inverse().dot(matrixSqrt.sqrt_inverse()), Inverse.of(g));
  }

  @Test
  void testSimple() {
    Tensor a = Tensors.fromString("{{4, 10}, {0, 9}}");
    MatrixSqrt matrixSqrt = MatrixSqrt.of(a);
    _check(a, matrixSqrt);
    Tolerance.CHOP.requireClose(matrixSqrt.sqrt(), Tensors.fromString("{{2, 2}, {0, 3}}"));
  }

  @RepeatedTest(5)
  void testIdentity(RepetitionInfo repetitionInfo) {
    int n = repetitionInfo.getCurrentRepetition();
    Tensor x = IdentityMatrix.of(n);
    _check(x, MatrixSqrt.of(x));
    _check(x, MatrixSqrt.ofSymmetric(x));
  }

  @Test
  void testTrapezoidalNormal() {
    Random random = new Random(1);
    Distribution distribution = TrapezoidalDistribution.of(-3, -1, 1, 3);
    for (int n = 1; n < 7; ++n) {
      Tensor x = RandomVariate.of(distribution, random, n, n);
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.of(x2));
    }
  }

  @Test
  void testRandomDiscreteUniform() {
    Random random = new Random(1);
    for (int n = 1; n < 10; ++n) {
      Tensor x = RandomVariate.of(DiscreteUniformDistribution.of(-200, 200), random, n, n);
      Tensor x2 = x.dot(x);
      MatrixSqrt sqrt = new DenmanBeaversDet(x2, Tolerance.CHOP);
      _check(x2, sqrt);
    }
  }

  @Test
  void testRandomSymmetric() {
    Random random = new Random(1);
    for (int n = 1; n < 5; ++n) {
      Tensor x = Symmetrize.of(RandomVariate.of(NormalDistribution.of(0, 0.2), random, n, n));
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.of(x2));
      _check(x2, MatrixSqrt.ofSymmetric(x2));
    }
  }

  @Test
  void testRandomSymmetricQuantity() {
    Distribution distribution = NormalDistribution.of(Quantity.of(0, "m"), Quantity.of(0.2, "m"));
    Random random = new Random(1);
    for (int n = 1; n < 5; ++n) {
      Tensor matrix = RandomVariate.of(distribution, random, n, n);
      Tensor x = Symmetrize.of(matrix);
      Tensor x2 = x.dot(x);
      _check(x2, MatrixSqrt.ofSymmetric(x2));
    }
  }

  @Test
  void testHermitian() {
    Tensor matrix = Tensors.fromString("{{0, I}, {-I, 0}}");
    MatrixSqrt matrixSqrt = MatrixSqrt.ofHermitian(matrix);
    _check(matrix, matrixSqrt);
  }

  @Test
  void testSymNegativeDiagonal() {
    Tensor matrix = DiagonalMatrix.of(-1, -2, -3);
    _check(matrix, MatrixSqrt.of(matrix));
    _check(matrix, MatrixSqrt.ofSymmetric(matrix));
  }

  @Test
  void testZeros() {
    Tensor matrix = Array.zeros(2, 2);
    MatrixSqrt matrixSqrt = MatrixSqrt.of(matrix);
    assertEquals(matrixSqrt.sqrt(), Array.zeros(2, 2));
    assertThrows(ArithmeticException.class, () -> matrixSqrt.sqrt_inverse());
  }

  @Test
  void testQuantity() {
    Tensor matrix = Tensors.fromString("{{10[m^2], -2[m^2]}, {-2[m^2], 4[m^2]}}");
    MatrixSqrt matrixSqrt = MatrixSqrt.of(matrix);
    Tensor eye = Dot.of(matrixSqrt.sqrt(), matrixSqrt.sqrt_inverse());
    Tolerance.CHOP.requireClose(eye, IdentityMatrix.of(2));
  }

  @Test
  void testComplexFail() {
    Tensor matrix = Tensors.fromString("{{I, 0}, {0, I}}");
    SymmetricMatrixQ.require(matrix);
    MatrixSqrt.of(matrix);
    assertThrows(ClassCastException.class, () -> MatrixSqrt.ofSymmetric(matrix));
  }

  @Test
  void testNonSquareFail() {
    assertThrows(IllegalArgumentException.class, () -> MatrixSqrt.of(RandomVariate.of(UniformDistribution.of(-2, 2), 2, 3)));
    assertThrows(IllegalArgumentException.class, () -> MatrixSqrt.of(HilbertMatrix.of(2, 3)));
  }

  @Test
  void testNonSymmetricFail() {
    assertThrows(TensorRuntimeException.class, () -> MatrixSqrt.ofSymmetric(RandomVariate.of(UniformDistribution.of(-2, 2), 4, 4)));
  }
}
