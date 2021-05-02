// code by jph
package ch.alpine.tensor.mat;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class LowerTriangularizeTest extends TestCase {
  public void test1x1() {
    Tensor matrix = Tensors.fromString("{{1}}");
    for (int k = -3; k <= 3; ++k)
      assertEquals(Tensors.fromString("{{" + (0 <= k ? 1 : 0) + "}}"), LowerTriangularize.of(matrix, k));
  }

  public void testIncludingDiagonal() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 5, 2}}");
    Tensor actual = Tensors.fromString("{{1, 0, 0}, {4, 5, 0}, {7, 8, 9}, {9, 5, 2}}");
    assertEquals(LowerTriangularize.of(matrix), actual);
  }

  public void testSubDiagonal() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 5, 2}}");
    Tensor actual = Tensors.fromString("{{0, 0, 0}, {4, 0, 0}, {7, 8, 0}, {9, 5, 2}}");
    assertEquals(LowerTriangularize.of(matrix, -1), actual);
  }

  public void testScalarFail() {
    AssertFail.of(() -> LowerTriangularize.of(RealScalar.ONE, 0));
  }

  public void testRank3Fail() {
    AssertFail.of(() -> LowerTriangularize.of(LeviCivitaTensor.of(3), 0));
  }
}
