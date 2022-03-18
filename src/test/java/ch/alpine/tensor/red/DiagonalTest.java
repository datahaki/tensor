// code by jph
package ch.alpine.tensor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.MatrixQ;
import ch.alpine.tensor.usr.AssertFail;

public class DiagonalTest {
  @Test
  public void testVector() {
    Tensor tensor = Diagonal.of(Range.of(10, 20));
    assertTrue(Tensors.isEmpty(tensor));
  }

  @Test
  public void testSpecial() {
    assertEquals(Diagonal.of(IdentityMatrix.of(5)), Tensors.vector(1, 1, 1, 1, 1));
    assertEquals(Diagonal.of(HilbertMatrix.of(4)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
  }

  @Test
  public void testRectangular() {
    assertEquals(Diagonal.of(HilbertMatrix.of(4, 5)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
    assertEquals(Diagonal.of(HilbertMatrix.of(5, 4)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
    assertEquals(Diagonal.of(Array.zeros(5, 12)), Array.zeros(5));
  }

  @Test
  public void testLieAlgebra() {
    assertTrue(MatrixQ.of(Diagonal.of(LeviCivitaTensor.of(3))));
  }

  @Test
  public void testCase1() {
    Tensor tensor = Tensors.fromString("{{2}, 3}");
    Tensor diagonal = Diagonal.mathematica(tensor);
    assertEquals(diagonal, Tensors.vector(2));
  }

  @Test
  public void testCase2() {
    Tensor tensor = Tensors.fromString("{{2}, {3, 4}}");
    Tensor diagonal = Diagonal.mathematica(tensor);
    assertEquals(diagonal, Tensors.vector(2, 4));
  }

  @Test
  public void testCase3() {
    Tensor tensor = Tensors.fromString("{{2}, {3, 4},{5,6,7}}");
    Tensor diagonal = Diagonal.mathematica(tensor);
    assertEquals(diagonal, Tensors.vector(2, 4, 7));
  }

  @Test
  public void testCase4() {
    Tensor tensor = Tensors.fromString("{{2}, {3},{5,6,7}}");
    Tensor diagonal = Diagonal.mathematica(tensor);
    assertEquals(diagonal, Tensors.vector(2));
  }

  @Test
  public void testCase5() {
    assertEquals(Diagonal.of(HilbertMatrix.of(4, 5)), Diagonal.mathematica(HilbertMatrix.of(4, 5)));
    assertEquals(Diagonal.of(HilbertMatrix.of(5, 4)), Diagonal.mathematica(HilbertMatrix.of(5, 4)));
  }

  @Test
  public void testFailScalar() {
    AssertFail.of(() -> Diagonal.of(RealScalar.ONE));
  }
}
