// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class LinearSolveTest {
  private static final Random RANDOM = new Random();

  @Test
  public void testSolveCR() {
    int n = 5 + RANDOM.nextInt(6);
    Tensor A = Tensors.matrix((i, j) -> //
    ComplexScalar.of( //
        RealScalar.of(RANDOM.nextInt(15)), //
        RealScalar.of(RANDOM.nextInt(15))), n, n);
    if (Scalars.nonZero(Det.of(A))) {
      Tensor b = Tensors.matrix((i, j) -> RationalScalar.of(i.equals(j) ? 1 : 0, 1), n, n + 3);
      Tensor X = LinearSolve.of(A, b);
      Tensor err = A.dot(X).subtract(b);
      assertEquals(err, b.map(Scalar::zero));
      assertEquals(err, Array.zeros(Dimensions.of(b)));
      ExactTensorQ.require(X);
    }
  }

  @Test
  public void testSolveRC() {
    int n = 5 + RANDOM.nextInt(6);
    Tensor A = Tensors.matrix((i, j) -> //
    RationalScalar.of(RANDOM.nextInt(100), RANDOM.nextInt(100) + 1), n, n);
    if (Scalars.nonZero(Det.of(A))) {
      Tensor b = Tensors.matrix((i, j) -> ComplexScalar.of(//
          RealScalar.of(RANDOM.nextInt(15)), //
          RealScalar.of(RANDOM.nextInt(15))), n, n + 3);
      Tensor X = LinearSolve.of(A, b);
      Tensor err = A.dot(X).subtract(b);
      assertEquals(err, b.map(Scalar::zero));
      assertEquals(err, Array.zeros(Dimensions.of(b)));
      ExactTensorQ.require(X);
    }
  }

  @Test
  public void testSolveDC() {
    Random random = new Random(123);
    int n = 7;
    Tensor A = Tensors.matrix((i, j) -> DoubleScalar.of(4 * random.nextGaussian() - 2), n, n);
    Tensor b = Tensors.matrix((i, j) -> ComplexScalar.of( //
        RealScalar.of(random.nextDouble()), //
        RealScalar.of(random.nextDouble())), n, n - 2);
    Tensor X = LinearSolve.of(A, b);
    Tensor err = A.dot(X).add(b.negate());
    Tolerance.CHOP.requireClose(err, b.multiply(RealScalar.ZERO));
    Tolerance.CHOP.requireClose(err, Array.zeros(Dimensions.of(b)));
  }

  @Test
  public void testGauss() {
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
  public void testIdentity() {
    int n = 5;
    Tensor A = Tensors.matrix((i, j) -> //
    RationalScalar.of(RANDOM.nextInt(100) - 50, RANDOM.nextInt(100) + 1), n, n);
    if (Scalars.nonZero(Det.of(A))) {
      Tensor b = IdentityMatrix.of(n);
      Tensor X = LinearSolve.of(A, b);
      assertEquals(X.dot(A), b);
      assertEquals(A.dot(X), b);
      ExactTensorQ.require(X);
    }
  }

  @Test
  public void testEmpty() {
    Tensor m = Tensors.matrix(new Number[][] { {} });
    Tensor b = Tensors.vector(new Number[] {});
    AssertFail.of(() -> LinearSolve.of(m, b));
  }

  @Test
  public void testEps() {
    Tensor m = Tensors.matrixDouble(new double[][] { { Double.MIN_VALUE } });
    Tensor b = Tensors.vectorDouble(new double[] { Double.MIN_VALUE });
    Tensor r = LinearSolve.of(m, b);
    assertEquals(r, Tensors.vector(1));
    assertEquals(Det.of(m), DoubleScalar.of(Double.MIN_VALUE));
  }

  @Test
  public void testQuantity1() {
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
  public void testQuantity2() {
    Scalar qs1 = Quantity.of(3, "m");
    Scalar qs2 = Quantity.of(4, "s");
    Tensor mat = Tensors.matrix(new Scalar[][] { { qs1 } });
    Tensor rhs = Tensors.of(qs2);
    Tensor sol = LinearSolve.of(mat, rhs);
    Tensor res = mat.dot(sol);
    assertEquals(res, rhs);
    ExactTensorQ.require(sol);
  }
}
