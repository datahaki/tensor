// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixQ;

class DiagonalTest {
  @Test
  void testVector() {
    Tensor tensor = Diagonal.of(Range.of(10, 20));
    assertTrue(Tensors.isEmpty(tensor));
  }

  @Test
  void testSpecial() {
    assertEquals(Diagonal.of(IdentityMatrix.of(5)), Tensors.vector(1, 1, 1, 1, 1));
    assertEquals(Diagonal.of(HilbertMatrix.of(4)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
  }

  @Test
  void testRectangular() {
    assertEquals(Diagonal.of(HilbertMatrix.of(4, 5)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
    assertEquals(Diagonal.of(HilbertMatrix.of(5, 4)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
    assertEquals(Diagonal.of(Array.zeros(5, 12)), Array.zeros(5));
  }

  @Test
  void testLieAlgebra() {
    assertTrue(MatrixQ.of(Diagonal.of(LeviCivitaTensor.of(3))));
  }

  @Test
  void testCase1() {
    Tensor tensor = Tensors.fromString("{{2}, 3}");
    Tensor diagonal = Diagonal.mathematica(tensor);
    assertEquals(diagonal, Tensors.vector(2));
  }

  @Test
  void testCase2() {
    Tensor tensor = Tensors.fromString("{{2}, {3, 4}}");
    Tensor diagonal = Diagonal.mathematica(tensor);
    assertEquals(diagonal, Tensors.vector(2, 4));
  }

  @Test
  void testCase3() {
    Tensor tensor = Tensors.fromString("{{2}, {3, 4},{5,6,7}}");
    Tensor diagonal = Diagonal.mathematica(tensor);
    assertEquals(diagonal, Tensors.vector(2, 4, 7));
  }

  @Test
  void testCase4() {
    Tensor tensor = Tensors.fromString("{{2}, {3},{5,6,7}}");
    Tensor diagonal = Diagonal.mathematica(tensor);
    assertEquals(diagonal, Tensors.vector(2));
  }

  @Test
  void testSides1() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3, 4}, {5, 6, 7, 8}}");
    assertEquals(Diagonal.of(matrix, 4), Tensors.empty());
    assertEquals(Diagonal.of(matrix, 3), Tensors.vector(4));
    assertEquals(Diagonal.of(matrix, 2), Tensors.vector(3, 8));
    assertEquals(Diagonal.of(matrix, 1), Tensors.vector(2, 7));
    assertEquals(Diagonal.of(matrix, 0), Tensors.vector(1, 6));
    assertEquals(Diagonal.of(matrix, -1), Tensors.vector(5));
    assertEquals(Diagonal.of(matrix, -2), Tensors.empty());
    assertEquals(Diagonal.of(matrix, -3), Tensors.empty());
  }

  @Test
  void testSides2() {
    Tensor matrix = Tensors.fromString("{{1, 5}, {2, 6}, {3, 7}, {4, 8}}");
    assertEquals(Diagonal.of(matrix, 2), Tensors.empty());
    assertEquals(Diagonal.of(matrix, 1), Tensors.vector(5));
    assertEquals(Diagonal.of(matrix, 0), Tensors.vector(1, 6));
    assertEquals(Diagonal.of(matrix, -1), Tensors.vector(2, 7));
    assertEquals(Diagonal.of(matrix, -2), Tensors.vector(3, 8));
    assertEquals(Diagonal.of(matrix, -3), Tensors.vector(4));
    assertEquals(Diagonal.of(matrix, -4), Tensors.empty());
  }

  @Test
  void testCase5() {
    assertEquals(Diagonal.of(HilbertMatrix.of(4, 5)), Diagonal.mathematica(HilbertMatrix.of(4, 5)));
    assertEquals(Diagonal.of(HilbertMatrix.of(5, 4)), Diagonal.mathematica(HilbertMatrix.of(5, 4)));
  }

  @Test
  void testFailScalar() {
    assertThrows(Throw.class, () -> Diagonal.of(RealScalar.ONE));
  }
}
