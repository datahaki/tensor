// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Slash;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.num.Cycles;
import ch.alpine.tensor.num.PermutationList;

class PermuteTest {
  @Test
  void test() {
    int[] perm = new int[] { 2, 3, 1, 0 };
    TensorUnaryOperator tuo = Permute.of(perm);
    Tensor P = Slash.of(IdentityMatrix.of(4), tuo);
    Tensor r = Range.of(0, 4).dot(P);
    int[] intArray = Primitives.toIntArray(r);
    assertArrayEquals(perm, intArray);
  }

  @Test
  void testCycles() {
    Cycles cycles = Cycles.of("{{1, 20}, {4, 10, 19, 6, 18}, {5, 9}, {7, 14, 13}}");
    int N = 30;
    int[] perm = PermutationList.of(cycles, N);
    TensorUnaryOperator tuo = Permute.of(perm);
    Tensor P = Slash.of(IdentityMatrix.of(N), tuo);
    Tensor r = Range.of(0, N).dot(P);
    int[] intArray = Primitives.toIntArray(r);
    assertArrayEquals(perm, intArray);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> Permute.of(Tensors.vector(3)));
  }
}
