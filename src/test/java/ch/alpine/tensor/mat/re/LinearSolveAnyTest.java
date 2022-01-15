// code by jph
package ch.alpine.tensor.mat.re;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LinearSolveAnyTest extends TestCase {
  public void testSome1() {
    Tensor m = Tensors.fromString("{{1, 2, 3}, {5, 6, 7}, {7, 8, 9}}");
    Tensor b = Tensors.fromString("{1, 1, 1}");
    Tensor x = LinearSolve.any(m, b);
    assertEquals(x, Tensors.fromString("{-1, 1, 0}"));
  }

  public void testDiag() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    Tensor x = LinearSolve.any(DiagonalMatrix.with(vector), vector);
    assertEquals(x, Tensors.fromString("{1, 1, 0, 1, 1, 1}"));
  }

  public void testDiag2() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    Tensor m = Join.of(DiagonalMatrix.with(vector), Array.zeros(3, 6));
    Tensor b = Join.of(vector, Array.zeros(3));
    Tensor x = LinearSolve.any(m.unmodifiable(), b.unmodifiable());
    assertEquals(m.dot(x), b);
  }

  public void testDiag2b() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    // m is 6 x 9 matrix
    Tensor m = Join.of(1, DiagonalMatrix.with(vector), Array.zeros(6, 3));
    Tensor b = Join.of(vector);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
    AssertFail.of(() -> Det.of(m));
  }

  public void testDiag3() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    Tensor m = Join.of(Array.zeros(3, 6), DiagonalMatrix.with(vector));
    Tensor b = Join.of(Array.zeros(3), vector);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
  }

  public void testDiag3b() {
    Tensor vector = Tensors.vector(3, 2, 0, 5, 4, 7);
    Tensor m = Join.of(1, Array.zeros(6, 3), DiagonalMatrix.with(vector));
    Tensor b = Join.of(vector);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
  }

  public void testSome2() {
    Tensor m = Tensors.fromString("{{1, 2, 3}, {5, 6, 7}, {7, 8, 9}}");
    Tensor b = Tensors.fromString("{1, -2, 1}");
    AssertFail.of(() -> LinearSolve.any(m, b));
  }

  public void testAny() {
    Tensor m = Tensors.fromString("{{1, 0, -1}, {0, 1, 0}, {1, 0, -1}}");
    Tensor b = Tensors.fromString("{0, 0, 0}");
    Tensor x = LinearSolve.any(m, b);
    Scalar det = Det.of(m);
    assertEquals(det, RealScalar.ZERO);
    assertEquals(x, b);
    assertEquals(m.dot(x), b);
  }

  public void testConstants() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(RealScalar.ONE, n, k);
      Tensor b = ConstantArray.of(RealScalar.ONE, n);
      Tensor x = LinearSolve.any(m, b);
      assertEquals(m.dot(x), b);
    }
  }

  public void testConstantsMN() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(RealScalar.of(1.0), n, k);
      Tensor b = ConstantArray.of(RealScalar.ONE, n);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  public void testConstantsVN() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(RealScalar.ONE, n, k);
      Tensor b = ConstantArray.of(RealScalar.of(1.0), n);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  public void testConstantsUW() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(Quantity.of(2, "m"), n, k);
      Tensor b = ConstantArray.of(Quantity.of(3, "m"), n);
      Tensor x = LinearSolve.any(m, b);
      assertEquals(m.dot(x), b);
    }
  }

  public void testConstantsUWM() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(Quantity.of(2, "m"), n, k);
      Tensor b = ConstantArray.of(Quantity.of(3, "m"), n, 2);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  public void testConstantsN() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(RealScalar.of(1.0), n, k);
      Tensor b = ConstantArray.of(RealScalar.of(1.0), n);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  public void testConstantsNUW() {
    int n = 3;
    for (int k = 1; k < 6; ++k) {
      Tensor m = ConstantArray.of(Quantity.of(2.0, "m"), n, k);
      Tensor b = ConstantArray.of(Quantity.of(3.0, "m"), n);
      Tensor x = LinearSolve.any(m, b);
      Tolerance.CHOP.requireClose(m.dot(x), b);
    }
  }

  public void testAny2() {
    Tensor m = Tensors.fromString("{{1}, {1}, {-1}}");
    Tensor b = Tensors.vector(2, 2, -2);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
    AssertFail.of(() -> Det.of(m)); // fail is consistent with Mathematica 12
  }

  public void testAnyN() {
    Tensor m = Tensors.fromString("{{1}, {1}, {-5}}");
    Tensor b = Tensors.vector(-2, -2, 10);
    Tensor x = LinearSolve.any(m, b);
    assertEquals(m.dot(x), b);
    AssertFail.of(() -> Det.of(m)); // fail is consistent with Mathematica 12
  }

  public void testLarge() {
    Distribution distribution = NormalDistribution.standard();
    Tensor m = RandomVariate.of(distribution, 2, 4);
    Tensor g = RandomVariate.of(distribution, 4);
    Tensor b = m.dot(g);
    Tensor x = LinearSolve.any(m, b);
    Tolerance.CHOP.requireClose(m.dot(x), b);
  }

  public void testNoSolutionFail() {
    AssertFail.of(() -> LinearSolve.any(Tensors.fromString("{{0}}"), Tensors.vector(1)));
    AssertFail.of(() -> LinearSolve.any(Tensors.fromString("{{0}}"), Tensors.vector(1.0)));
  }
}
