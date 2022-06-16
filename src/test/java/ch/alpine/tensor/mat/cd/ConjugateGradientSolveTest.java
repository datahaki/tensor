// code by jph
package ch.alpine.tensor.mat.cd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.qty.Quantity;

class ConjugateGradientSolveTest {
  @Test
  void testSimple() {
    Tensor matrix = Tensors.matrix(new Number[][] { //
        { 25, 15, -5 }, //
        { 15, 18, 0 }, //
        { -5, 0, 11 } //
    });
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    Tensor b = Tensors.vector(1, 2, 3);
    Tensor x = ConjugateGradientSolve.of(matrix, b);
    assertEquals(matrix.dot(x), b);
    ExactTensorQ.require(x);
  }

  @Test
  void testUnits1() {
    ScalarUnaryOperator suo = s -> Quantity.of(s, "m^2");
    Tensor matrix = Tensors.fromString("{{60, 30, 20}, {30, 20, 15}, {20, 15, 12}}").map(suo);
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    Tensor b = Tensors.vector(1, 2, 3).map(suo);
    Tensor x = LinearSolve.of(matrix, b);
    assertEquals(matrix.dot(x), b);
    x = ConjugateGradientSolve.of(matrix, b);
    assertEquals(matrix.dot(x), b);
    ExactTensorQ.require(x);
  }

  @Test
  void testUnits2() {
    ScalarUnaryOperator suo = s -> Quantity.of(s, "m^2");
    Tensor matrix = Tensors.fromString("{{60, 30, 20}, {30, 20, 15}, {20, 15, 12}}").map(suo);
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    Tensor b = Tensors.vector(1, 2, 3);
    Tensor x = LinearSolve.of(matrix, b);
    assertEquals(matrix.dot(x), b);
    x = ConjugateGradientSolve.of(matrix, b);
    assertEquals(matrix.dot(x), b);
    ExactTensorQ.require(x);
  }

  @Test
  void testMixedUnits() {
    Tensor matrix = Tensors.fromString("{{10[m^2], 1[m*kg]}, {1[m*kg], 10[kg^2]}}");
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    Tensor b = Tensors.fromString("{4[m], 3[kg]}");
    // Tensor x =
    CholeskyDecomposition.of(matrix).solve(b);
    // Tensor x2 = ConjugateGradientSolve.of(matrix, b);
    // ExactTensorQ.require(x2);
    // assertEquals(x, x2);
  }

  @Test
  void testComplex() {
    Tensor matrix = Tensors.fromString("{{10[m^2], I[m*kg]}, {-I[m*kg], 10[kg^2]}}");
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    Tensor b = Tensors.fromString("{4[m], 3[kg]}");
    Tensor x = CholeskyDecomposition.of(matrix).solve(b);
    assertEquals(matrix.dot(x), b);
    Tensor x1 = LinearSolve.of(matrix, b);
    assertEquals(x, x1);
    // Tensor x2 = ConjugateGradientSolve.of(matrix, b);
    // ExactTensorQ.require(x2);
    // assertEquals(x, x2);
  }

  @Test
  void testComplex2() {
    Tensor matrix = Tensors.fromString("{{10[m^2], I[m*kg]}, {-I[m*kg], 10[kg^2]}}");
    assertTrue(PositiveDefiniteMatrixQ.ofHermitian(matrix));
    Tensor b = Tensors.fromString("{2+I/5[m], 3-I[kg]}");
    Tensor x = CholeskyDecomposition.of(matrix).solve(b);
    Tensor x1 = LinearSolve.of(matrix, b);
    assertEquals(x, x1);
    // Tensor x2 = ConjugateGradientSolve.of(matrix, b);
    // ExactTensorQ.require(x2);
    // System.out.println(x);
    // System.out.println(x2);
    // assertEquals(x, x2);
  }

  @Test
  void testLengthMismatchFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> ConjugateGradientSolve.of(IdentityMatrix.of(3), Tensors.vector(1, 2)));
    assertThrows(IllegalArgumentException.class, () -> ConjugateGradientSolve.of(IdentityMatrix.of(3), Tensors.vector(1, 2, 3, 4)));
  }

  @Test
  void testRhsMatrixFail() {
    assertThrows(ClassCastException.class, () -> ConjugateGradientSolve.of(IdentityMatrix.of(2), IdentityMatrix.of(2)));
  }

  @Test
  void testEmptyFail() {
    assertThrows(IllegalArgumentException.class, () -> ConjugateGradientSolve.of(Tensors.empty(), Tensors.vector(1)));
  }
}
