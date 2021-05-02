// code by jph
package ch.alpine.tensor.red;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DiagonalTest extends TestCase {
  public void testVector() {
    Tensor tensor = Diagonal.of(Range.of(10, 20));
    assertTrue(Tensors.isEmpty(tensor));
  }

  public void testSpecial() {
    assertEquals(Diagonal.of(IdentityMatrix.of(5)), Tensors.vector(1, 1, 1, 1, 1));
    assertEquals(Diagonal.of(HilbertMatrix.of(4)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
  }

  public void testRectangular() {
    assertEquals(Diagonal.of(HilbertMatrix.of(4, 5)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
    assertEquals(Diagonal.of(HilbertMatrix.of(5, 4)), Tensors.vector(1, 3, 5, 7).map(Scalar::reciprocal));
    assertEquals(Diagonal.of(Array.zeros(5, 12)), Array.zeros(5));
  }

  public void testLieAlgebra() {
    assertTrue(MatrixQ.of(Diagonal.of(LeviCivitaTensor.of(3))));
  }

  public void testFailScalar() {
    AssertFail.of(() -> Diagonal.of(RealScalar.ONE));
  }
}
