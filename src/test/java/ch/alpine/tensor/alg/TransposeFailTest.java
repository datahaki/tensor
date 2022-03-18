// code by jph
package ch.alpine.tensor.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.usr.AssertFail;

public class TransposeFailTest {
  @Test
  public void testScalarFail() {
    Tensor v = DoubleScalar.NEGATIVE_INFINITY;
    AssertFail.of(() -> Transpose.of(v));
    AssertFail.of(() -> Transpose.of(v, new int[] { 2 }));
  }

  @Test
  public void testEmptyFail() {
    AssertFail.of(() -> Transpose.of(Tensors.empty()));
  }

  @Test
  public void testVectorFail() {
    AssertFail.of(() -> Transpose.of(Tensors.vector(2, 3, 4, 5)));
  }

  @Test
  public void testEmpty2() {
    Tensor empty2 = Tensors.fromString("{{}, {}}");
    assertEquals(Transpose.of(empty2), Tensors.empty());
    AssertFail.of(() -> Transpose.of(Transpose.of(empty2)));
  }

  @Test
  public void testRankFail() {
    Transpose.of(Array.zeros(1, 3, 2), 1, 2, 0);
    Transpose.of(Array.zeros(3, 3, 3), 1, 0);
    AssertFail.of(() -> Transpose.of(Array.zeros(3, 3, 2), 3, 2, 1, 0));
  }

  @Test
  public void testFail2() {
    AssertFail.of(() -> Transpose.of(Tensors.fromString("{{1, 2}, {3, 4, 5}}")));
    AssertFail.of(() -> Transpose.of(Tensors.fromString("{{1, 2, 3}, {4, 5}}")));
  }

  @Test
  public void testNonPermutationFail() {
    Tensor matrix = Array.zeros(2, 3);
    AssertFail.of(() -> Transpose.of(matrix, 1));
    AssertFail.of(() -> Transpose.of(matrix, 2, 0));
    AssertFail.of(() -> Transpose.of(matrix, 0, -1));
  }

  @Test
  public void testNonPermFail1() {
    Tensor matrix = Array.zeros(2, 3);
    AssertFail.of(() -> Transpose.of(matrix, 0, 0));
    AssertFail.of(() -> Transpose.of(matrix, 1, 1));
  }

  @Test
  public void testNonPermFail2() {
    Tensor matrix = Array.zeros(3, 2);
    AssertFail.of(() -> Transpose.of(matrix, 0, 0));
    AssertFail.of(() -> Transpose.of(matrix, 1, 1));
  }

  @Test
  public void testNonPermFail3() {
    Tensor matrix = Array.zeros(3, 2, 1);
    AssertFail.of(() -> Transpose.of(matrix, 0, 1, 0));
    AssertFail.of(() -> Transpose.of(matrix, 1, 0, 1));
    AssertFail.of(() -> Transpose.of(matrix, 0, 1, 1));
    AssertFail.of(() -> Transpose.of(matrix, 2, 2, 1));
  }
}
