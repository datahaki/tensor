// code by jph
package ch.alpine.tensor.alg;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RotateRightTest extends TestCase {
  public void testVector() {
    Tensor vector = Tensors.vector(0, 1, 2, 3, 4).unmodifiable();
    assertEquals(RotateRight.of(vector, +6), Tensors.vector(4, 0, 1, 2, 3));
    assertEquals(RotateRight.of(vector, +1), Tensors.vector(4, 0, 1, 2, 3));
    assertEquals(RotateRight.of(vector, +0), Tensors.vector(0, 1, 2, 3, 4));
    assertEquals(RotateRight.of(vector, -1), Tensors.vector(1, 2, 3, 4, 0));
    assertEquals(RotateRight.of(vector, -2), Tensors.vector(2, 3, 4, 0, 1));
    assertEquals(RotateRight.of(vector, -7), Tensors.vector(2, 3, 4, 0, 1));
    assertEquals(vector, Range.of(0, 5));
  }

  public void testMatrix() {
    int size = 5;
    for (int k = 0; k < size * 2; ++k) {
      Tensor matrix = RotateRight.of(IdentityMatrix.of(size), -k);
      assertEquals(matrix.get(0), UnitVector.of(size, k % size));
    }
  }

  public void testEmpty() {
    assertEquals(RotateRight.of(Tensors.empty(), +1), Tensors.empty());
    assertEquals(RotateRight.of(Tensors.empty(), +0), Tensors.empty());
    assertEquals(RotateRight.of(Tensors.empty(), -1), Tensors.empty());
  }

  public void testFailScalar() {
    AssertFail.of(() -> RotateRight.of(RealScalar.ONE, 0));
  }

  public void testFailNull() {
    AssertFail.of(() -> RotateRight.of(null, 0));
  }
}
