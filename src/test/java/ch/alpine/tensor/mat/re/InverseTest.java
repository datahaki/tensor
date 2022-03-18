// code by jph
package ch.alpine.tensor.mat.re;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.fft.FourierMatrix;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.mat.HermitianMatrixQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;

public class InverseTest {
  @Test
  public void testInverse() {
    int n = 25;
    Tensor A = RandomVariate.of(NormalDistribution.standard(), n, n);
    Tensor Ai = Inverse.of(A);
    Tensor id = IdentityMatrix.of(A.length());
    Chop._09.requireClose(A.dot(Ai), id);
    Chop._09.requireClose(Ai.dot(A), id);
  }

  @Test
  public void testInverseNoAbs() {
    int n = 12;
    int p = 20357;
    Random random = new SecureRandom();
    Tensor A = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(p), p), n, n);
    int iter = 0;
    while (Scalars.isZero(Det.of(A, Pivots.FIRST_NON_ZERO)) && iter < 5) {
      A = Tensors.matrix((i, j) -> GaussScalar.of(random.nextInt(p), p), n, n);
      ++iter;
    }
    Tensor b = Tensors.vector(i -> GaussScalar.of(random.nextInt(p), p), n);
    Tensor x = LinearSolve.of(A, b, Pivots.FIRST_NON_ZERO);
    assertEquals(A.dot(x), b);
    Tensor id = DiagonalMatrix.of(n, GaussScalar.of(1, p));
    Tensor Ai = LinearSolve.of(A, id, Pivots.FIRST_NON_ZERO);
    assertEquals(A.dot(Ai), id);
    assertEquals(Ai.dot(A), id);
  }

  @Test
  public void testGeneralIdentity() {
    Tensor A = HilbertMatrix.of(3, 3);
    Tensor b = UnitVector.of(3, 1);
    Tensor x = LinearSolve.of(A, b);
    assertEquals(A.dot(x), b);
    assertEquals(Inverse.of(A).dot(b), x);
  }

  @Test
  public void testFourier() {
    Tensor inv1 = Inverse.of(FourierMatrix.of(5), Pivots.FIRST_NON_ZERO);
    Tensor inv2 = Inverse.of(FourierMatrix.of(5), Pivots.ARGMAX_ABS);
    Tolerance.CHOP.requireClose(inv1, inv2);
  }

  @Test
  public void testGaussian() {
    int prime = 3121;
    Distribution distribution = DiscreteUniformDistribution.of(0, prime);
    Scalar one = GaussScalar.of(1, prime);
    for (int n = 3; n < 6; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n).map(s -> GaussScalar.of(s.number().intValue(), prime));
      if (Scalars.nonZero(Det.of(matrix)))
        for (Pivot pivot : Pivots.values()) {
          Tensor revers = Inverse.of(matrix, pivot);
          MatrixQ.requireSize(revers, n, n);
          assertEquals(DiagonalMatrix.of(n, one), Dot.of(matrix, revers));
        }
    }
  }

  @Test
  public void testDet0() {
    Tensor matrix = ResourceData.of("/mat/det0-matlab.csv"); // det(matrix) == 0
    assertNotNull(matrix);
    assertThrows(TensorRuntimeException.class, () -> Inverse.of(matrix));
    assertThrows(TensorRuntimeException.class, () -> Inverse.of(N.DOUBLE.of(matrix)));
  }

  @Test
  public void testZeroFail() {
    Tensor matrix = DiagonalMatrix.of(1, 2, 0, 3);
    assertThrows(TensorRuntimeException.class, () -> Inverse.of(matrix));
    assertThrows(TensorRuntimeException.class, () -> Inverse.of(matrix, Pivots.FIRST_NON_ZERO));
  }

  @Test
  public void testFailNonSquare() {
    assertThrows(IllegalArgumentException.class, () -> Inverse.of(HilbertMatrix.of(3, 4)));
    assertThrows(IllegalArgumentException.class, () -> Inverse.of(HilbertMatrix.of(4, 3)));
  }

  @Test
  public void testFailRank3() {
    assertThrows(ClassCastException.class, () -> Inverse.of(LeviCivitaTensor.of(3)));
  }

  @Test
  public void testQuantity1() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(2, "m");
    Scalar qs3 = Quantity.of(3, "rad");
    Scalar qs4 = Quantity.of(4, "rad");
    Tensor ve1 = Tensors.of(qs1.multiply(qs1), qs2.multiply(qs3));
    Tensor ve2 = Tensors.of(qs2.multiply(qs3), qs4.multiply(qs4));
    Tensor mat = Tensors.of(ve1, ve2);
    Tensor inv = LinearSolve.of(mat, IdentityMatrix.of(2));
    Tensor res = mat.dot(inv);
    Tensor expect = Tensors.fromString("{{1, 0[m*rad^-1]}, {0[m^-1*rad], 1}}");
    assertEquals(res, expect);
    Tensor inverse = Inverse.of(mat);
    Tensor expected = Tensors.fromString( //
        "{{-4/5[m^-2], 3/10[m^-1*rad^-1]}, {3/10[m^-1*rad^-1], -1/20[rad^-2]}}");
    assertEquals(inverse, expected);
    ExactTensorQ.require(inverse);
  }

  @Test
  public void testQuantity2() {
    Tensor matrix = Tensors.fromString( //
        "{{1[m^2], 2[m*rad], 3[kg*m]}, {4[m*rad], 2[rad^2], 2[kg*rad]}, {5[kg*m], 1[kg*rad], 7[kg^2]}}");
    final Tensor eye = IdentityMatrix.of(3).unmodifiable();
    Tensor expect = Tensors.fromString("{{1, 0[m*rad^-1], 0[kg^-1*m]}, {0[m^-1*rad], 1, 0[kg^-1*rad]}, {0[kg*m^-1], 0[kg*rad^-1], 1}}");
    for (Pivot pivot : Pivots.values()) {
      Tensor inv = LinearSolve.of(matrix, eye, pivot);
      Tensor res = matrix.dot(inv);
      assertEquals(res, expect);
    }
    Tensor inverse = Inverse.of(matrix);
    assertEquals(matrix.dot(inverse), expect);
    Tensor dual = Tensors.fromString("{{1, 0[m^-1*rad], 0[kg*m^-1]}, {0[m*rad^-1], 1, 0[kg*rad^-1]}, {0[kg^-1*m], 0[kg^-1*rad], 1}}");
    assertEquals(inverse.dot(matrix), dual);
    assertFalse(HermitianMatrixQ.of(matrix));
    assertFalse(SymmetricMatrixQ.of(matrix));
  }

  @Test
  public void testQuantity3() { // confirmed with Mathematica 12
    Tensor matrix = Tensors.fromString("{{1[m], 1[s]}, {1[m], 2[s]}}");
    Tensor tensor = Inverse.of(matrix);
    // {{Quantity[2, 1/("Meters")], Quantity[-1, 1/("Meters")]}, {Quantity[-1, 1/("Seconds")], Quantity[1, 1/("Seconds")]}}
    Tensor expect = Tensors.fromString("{{2[m^-1], -1[m^-1]}, {-1[s^-1], 1[s^-1]}}");
    assertEquals(tensor, expect);
    Tensor eye = matrix.dot(tensor);
    assertEquals(eye, IdentityMatrix.of(2));
    Tensor eye2 = tensor.dot(matrix);
    // {{1, Quantity[0, ("Seconds")/("Meters")]}, {Quantity[0, ("Meters")/( "Seconds")], 1}}
    Tensor expec2 = Tensors.fromString("{{1, 0[m^-1*s]}, {0[m*s^-1], 1}}");
    assertEquals(expec2, eye2);
  }

  @Test
  public void testQuantity4() { // confirmed with Mathematica 12
    Tensor matrix = Tensors.fromString("{{1[m], 1[m]}, {1[s], 2[s]}}");
    Tensor tensor = Inverse.of(matrix);
    // {{Quantity[2, 1/("Meters")], Quantity[-1, 1/("Meters")]}, {Quantity[-1, 1/("Seconds")], Quantity[1, 1/("Seconds")]}}
    Tensor expect = Tensors.fromString("{{2[m^-1], -1[s^-1]}, {-1[m^-1], 1[s^-1]}}");
    assertEquals(tensor, expect);
    assertEquals(tensor.dot(matrix), IdentityMatrix.of(2));
    // {{1, Quantity[0, ("Meters")/("Seconds")]}, {Quantity[0, ("Seconds")/("Meters")], 1}}
    Tensor expec2 = Tensors.fromString("{{1, 0[m*s^-1]}, {0[m^-1*s], 1}}");
    assertEquals(expec2, matrix.dot(tensor));
  }

  @Test
  public void testMixed2x2() {
    Tensor matrix = Tensors.fromString("{{60[m^2], 30[m*rad]}, {30[m*rad], 20[rad^2]}}");
    Inverse.of(matrix);
  }

  @Test
  public void testMixed3x3() {
    Tensor matrix = Tensors.fromString( //
        "{{60[m^2], 30[m*rad], 20[kg*m]}, {30[m*rad], 20[rad^2], 15[kg*rad]}, {20[kg*m], 15[kg*rad], 12[kg^2]}}");
    SymmetricMatrixQ.require(matrix);
    Tensor inverse = Inverse.of(matrix);
    Tensor expect = Tensors.fromString("{{1, 0[m*rad^-1], 0[kg^-1*m]}, {0[m^-1*rad], 1, 0[kg^-1*rad]}, {0[kg*m^-1], 0[kg*rad^-1], 1}}");
    assertEquals(matrix.dot(inverse), expect);
    Tensor other = inverse.dot(matrix);
    assertEquals(other.get(0), Tensors.fromString("{1, 0[m^-1*rad], 0[kg*m^-1]}"));
    assertEquals(other.get(1), Tensors.fromString("{0[m*rad^-1], 1, 0[kg*rad^-1]}"));
    assertEquals(other.get(2), Tensors.fromString("{0[kg^-1*m], 0[kg^-1*rad], 1}"));
    ExactTensorQ.require(inverse);
  }

  @Test
  public void testDecimalScalarInverse() {
    Tensor matrix = HilbertMatrix.of(5).map(N.DECIMAL128);
    Tensor invers = Inverse.of(matrix);
    Scalar detmat = Det.of(matrix);
    Scalar detinv = Det.of(invers);
    Scalar one = detmat.multiply(detinv);
    assertTrue(one instanceof DecimalScalar);
    Tolerance.CHOP.requireClose(one, RealScalar.ONE);
  }
}
