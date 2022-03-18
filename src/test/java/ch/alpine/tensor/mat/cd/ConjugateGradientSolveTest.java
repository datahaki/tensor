// code by jph
package ch.alpine.tensor.mat.cd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;

public class ConjugateGradientSolveTest {
  @Test
  public void testSimple() {
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
  public void testUnits1() {
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
  public void testUnits2() {
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
  public void testMixedUnits() {
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
  public void testComplex() {
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
  public void testComplex2() {
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
  public void testLengthMismatchFail() {
    AssertFail.of(() -> ConjugateGradientSolve.of(IdentityMatrix.of(3), Tensors.vector(1, 2)));
    AssertFail.of(() -> ConjugateGradientSolve.of(IdentityMatrix.of(3), Tensors.vector(1, 2, 3, 4)));
  }

  @Test
  public void testRhsMatrixFail() {
    AssertFail.of(() -> ConjugateGradientSolve.of(IdentityMatrix.of(2), IdentityMatrix.of(2)));
  }

  @Test
  public void testEmptyFail() {
    AssertFail.of(() -> ConjugateGradientSolve.of(Tensors.empty(), Tensors.vector(1)));
  }
}
