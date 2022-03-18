// code by jph
package ch.alpine.tensor.mat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.usr.AssertFail;

public class LowerTriangularizeTest {
  @Test
  public void test1x1() {
    Tensor matrix = Tensors.fromString("{{1}}");
    for (int k = -3; k <= 3; ++k)
      assertEquals(Tensors.fromString("{{" + (0 <= k ? 1 : 0) + "}}"), LowerTriangularize.of(matrix, k));
  }

  @Test
  public void testIncludingDiagonal() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 5, 2}}");
    Tensor actual = Tensors.fromString("{{1, 0, 0}, {4, 5, 0}, {7, 8, 9}, {9, 5, 2}}");
    assertEquals(LowerTriangularize.of(matrix), actual);
  }

  @Test
  public void testSubDiagonal() {
    Tensor matrix = Tensors.fromString("{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {9, 5, 2}}");
    Tensor actual = Tensors.fromString("{{0, 0, 0}, {4, 0, 0}, {7, 8, 0}, {9, 5, 2}}");
    assertEquals(LowerTriangularize.of(matrix, -1), actual);
  }

  @Test
  public void testScalarFail() {
    AssertFail.of(() -> LowerTriangularize.of(RealScalar.ONE, 0));
  }

  @Test
  public void testRank3Fail() {
    AssertFail.of(() -> LowerTriangularize.of(LeviCivitaTensor.of(3), 0));
  }
}
