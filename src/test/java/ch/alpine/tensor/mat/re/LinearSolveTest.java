// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;

class LinearSolveTest {
  @Test
  void testSolveCR() {
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    int n = 5 + randomGenerator.nextInt(6);
    Tensor A = Tensors.matrix((_, _) -> //
    ComplexScalar.of( //
        RealScalar.of(randomGenerator.nextInt(15)), //
        RealScalar.of(randomGenerator.nextInt(15))), n, n);
    assumeTrue(Scalars.nonZero(Det.of(A)));
    Tensor b = Tensors.matrix((i, j) -> RationalScalar.of(i.equals(j) ? 1 : 0, 1), n, n + 3);
    Tensor X = LinearSolve.of(A, b);
    Tensor err = A.dot(X).subtract(b);
    assertEquals(err, b.map(Scalar::zero));
    assertEquals(err, Array.zeros(Dimensions.of(b)));
    ExactTensorQ.require(X);
  }

  @Test
  void testSolveRC() {
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    int n = 5 + randomGenerator.nextInt(6);
    Tensor A = Tensors.matrix((_, _) -> //
    RationalScalar.of(randomGenerator.nextInt(100), randomGenerator.nextInt(100) + 1), n, n);
    assumeTrue(Scalars.nonZero(Det.of(A)));
    Tensor b = Tensors.matrix((_, _) -> ComplexScalar.of(//
        RealScalar.of(randomGenerator.nextInt(15)), //
        RealScalar.of(randomGenerator.nextInt(15))), n, n + 3);
    Tensor X = LinearSolve.of(A, b);
    Tensor err = A.dot(X).subtract(b);
    assertEquals(err, b.map(Scalar::zero));
    assertEquals(err, Array.zeros(Dimensions.of(b)));
    ExactTensorQ.require(X);
  }

  @Test
  void testSolveDC() {
    Random random = new Random(123);
    int n = 7;
    Tensor A = Tensors.matrix((_, _) -> DoubleScalar.of(4 * random.nextGaussian() - 2), n, n);
    Tensor b = Tensors.matrix((_, _) -> ComplexScalar.of( //
        RealScalar.of(random.nextDouble()), //
        RealScalar.of(random.nextDouble())), n, n - 2);
    Tensor X = LinearSolve.of(A, b);
    Tensor err = A.dot(X).add(b.negate());
    Tolerance.CHOP.requireClose(err, b.multiply(RealScalar.ZERO));
    Tolerance.CHOP.requireClose(err, Array.zeros(Dimensions.of(b)));
  }

  @Test
  void testGauss() {
    Tensor vec1 = Tensors.vectorDouble(0, 2, 5.3);
    Tensor vec2 = Tensors.vectorDouble(-1.0, 3.1, 0.3);
    Tensor vec3 = Tensors.vectorDouble(2.0, 0.4, 0.3);
    Tensor b = Tensors.vectorDouble(0.3, 0.5, 0.7);
    Tensor A = Tensors.of(vec1, vec2, vec3);
    {
      Tensor x = LinearSolve.of(A, b);
      Tensor err = A.dot(x).add(b.negate());
      Tolerance.CHOP.requireClose(err, Tensors.vectorLong(0, 0, 0));
      Tolerance.CHOP.requireClose(err, Array.zeros(3));
    }
    Tensor eye2 = Tensors.of( //
        Tensors.vectorDouble(1.0, 0.0, 0.0, 3), //
        Tensors.vectorDouble(0.0, 0.0, 1.0, 5));
    Tensor eye3 = Tensors.of(eye2, eye2, eye2);
    Tensor sol = LinearSolve.of(A, eye3);
    {
      Tensor err = A.dot(sol).add(eye3.negate());
      Tolerance.CHOP.requireClose(err, eye3.multiply(DoubleScalar.of(0)));
      Tolerance.CHOP.requireClose(err, Array.zeros(Dimensions.of(eye3)));
    }
  }

  @Test
  void testIdentity() {
    RandomGenerator randomGenerator = ThreadLocalRandom.current();
    int n = 5;
    Tensor A = Tensors.matrix((_, _) -> //
    RationalScalar.of(randomGenerator.nextInt(100) - 50, randomGenerator.nextInt(100) + 1), n, n);
    assumeTrue(Scalars.nonZero(Det.of(A)));
    Tensor b = IdentityMatrix.of(n);
    Tensor X = LinearSolve.of(A, b);
    assertEquals(X.dot(A), b);
    assertEquals(A.dot(X), b);
    ExactTensorQ.require(X);
  }

  @Test
  void testEmpty() {
    Tensor m = Tensors.matrix(new Number[][] { {} });
    Tensor b = Tensors.vector(new Number[] {});
    assertThrows(IllegalArgumentException.class, () -> LinearSolve.of(m, b));
  }

  @Test
  void testEps() {
    Tensor m = Tensors.matrixDouble(new double[][] { { Double.MIN_VALUE } });
    Tensor b = Tensors.vectorDouble(new double[] { Double.MIN_VALUE });
    Tensor r = LinearSolve.of(m, b);
    assertEquals(r, Tensors.vector(1));
    assertEquals(Det.of(m), DoubleScalar.of(Double.MIN_VALUE));
  }

  @Test
  void testQuantity1() {
    final Scalar one = Quantity.of(1, "m");
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Scalar qs3 = Quantity.of(2, "m");
    Scalar qs4 = Quantity.of(-3, "m");
    Tensor ve1 = Tensors.of(qs1, qs2);
    Tensor ve2 = Tensors.of(qs3, qs4);
    Tensor mat = Tensors.of(ve1, ve2);
    Tensor eye = DiagonalMatrix.of(2, one);
    Tensor inv = LinearSolve.of(mat, eye);
    Tensor res = mat.dot(inv);
    assertEquals(res, eye);
    ExactTensorQ.require(inv);
  }

  @Test
  void testQuantity2() {
    Scalar qs1 = Quantity.of(3, "m");
    Scalar qs2 = Quantity.of(4, "s");
    Tensor mat = Tensors.matrix(new Scalar[][] { { qs1 } });
    Tensor rhs = Tensors.of(qs2);
    Tensor sol = LinearSolve.of(mat, rhs);
    Tensor res = mat.dot(sol);
    assertEquals(res, rhs);
    ExactTensorQ.require(sol);
  }

  @Test
  void testSome1() {
    Tensor m = Tensors.fromString("{{1, 2, 3}, {5, 6, 7}, {7, 8, 9}}");
    Tensor b = Tensors.fromString("{1, 1, 1}");
    Tensor x = LinearSolve.any(m, b);
    assertEquals(x, Tensors.fromString("{-1, 1, 0}"));
  }

  @Test
  void testDiag() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    Tensor x = LinearSolve.any(DiagonalMatrix.with(vector), vector);
    assertEquals(x, Tensors.fromString("{1, 1, 0, 1, 1, 1}"));
  }

  @Test
  void testDiag2() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    Tensor m = Join.of(DiagonalMatrix.with(vector), Array.zeros(3, 6));
    Tensor b = Join.of(vector, Array.zeros(3));
    Tensor x = LinearSolve.any(m.unmodifiable(), b.unmodifiable());
    assertEquals(m.dot(x), b);
  }

  @Test
  void testDiag2b() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    // m is 6 x 9 matrix
    Tensor m = Join.of(1, DiagonalMatrix.with(vector), Array.zeros(6, 3));
    Tensor b = Join.of(vector);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
    assertThrows(Throw.class, () -> Det.of(m));
  }

  @Test
  void testDiag3() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    Tensor m = Join.of(Array.zeros(3, 6), DiagonalMatrix.with(vector));
    Tensor b = Join.of(Array.zeros(3), vector);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
  }

  @Test
  void testDiag3b() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    Tensor m = Join.of(1, Array.zeros(6, 3), DiagonalMatrix.with(vector));
    Tensor b = Join.of(vector);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
  }

  @Test
  void testSome2() {
    Tensor m = Tensors.fromString("{{1, 2, 3}, {5, 6, 7}, {7, 8, 9}}");
    Tensor b = Tensors.fromString("{1, -2, 1}");
    assertThrows(Throw.class, () -> LinearSolve.any(m, b));
  }

  @Test
  void testAny() {
    Tensor m = Tensors.fromString("{{1, 0, -1}, {0, 1, 0}, {1, 0, -1}}");
    Tensor b = Tensors.fromString("{0, 0, 0}");
    Tensor x = LinearSolve.any(m, b);
    Scalar det = Det.of(m);
    assertEquals(det, RealScalar.ZERO);
    assertEquals(x, b);
    assertEquals(m.dot(x), b);
  }

  @Test
  void testConstants() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(RealScalar.ONE, n, k);
      Tensor b = ConstantArray.of(RealScalar.ONE, n);
      Tensor x = LinearSolve.any(m, b);
      assertEquals(m.dot(x), b);
    }
  }

  @Test
  void testConstantsMN() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(RealScalar.of(1.0), n, k);
      Tensor b = ConstantArray.of(RealScalar.ONE, n);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  @Test
  void testConstantsVN() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(RealScalar.ONE, n, k);
      Tensor b = ConstantArray.of(RealScalar.of(1.0), n);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  @Test
  void testConstantsUW() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(Quantity.of(2, "m"), n, k);
      Tensor b = ConstantArray.of(Quantity.of(3, "m"), n);
      Tensor x = LinearSolve.any(m, b);
      assertEquals(m.dot(x), b);
    }
  }

  @Test
  void testConstantsUWM() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(Quantity.of(2, "m"), n, k);
      Tensor b = ConstantArray.of(Quantity.of(3, "m"), n, 2);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  @Test
  void testConstantsN() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(RealScalar.of(1.0), n, k);
      Tensor b = ConstantArray.of(RealScalar.of(1.0), n);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  @Test
  void testConstantsNUW() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(Quantity.of(2.0, "m"), n, k);
      Tensor b = ConstantArray.of(Quantity.of(3.0, "m"), n);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  @Test
  void testAny2() {
    Tensor m = Tensors.fromString("{{1}, {1}, {-1}}");
    Tensor b = Tensors.vector(2, 2, -2);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
    assertThrows(Throw.class, () -> Det.of(m)); // fail is consistent with Mathematica 12
  }

  @Test
  void testAnyN() {
    Tensor m = Tensors.fromString("{{1}, {1}, {-5}}");
    Tensor b = Tensors.vector(-2, -2, 10);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
    assertThrows(Throw.class, () -> Det.of(m)); // fail is consistent with Mathematica 12
  }

  @Test
  void testLarge() {
    Distribution distribution = NormalDistribution.standard();
    Tensor m = RandomVariate.of(distribution, 2, 4);
    Tensor g = RandomVariate.of(distribution, 4);
    Tensor b = m.dot(g);
    Tensor x = LinearSolve.any(m, b);
    Tolerance.CHOP.requireClose(m.dot(x), b);
  }

  @Test
  void testNoSolutionFail() {
    assertThrows(Throw.class, () -> LinearSolve.any(Tensors.fromString("{{0}}"), Tensors.vector(1)));
    assertThrows(Throw.class, () -> LinearSolve.any(Tensors.fromString("{{0}}"), Tensors.vector(1.0)));
  }
}
