// code by jph
package ch.alpine.tensor.opt.hun;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.RandomPermutation;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class BipartitionImplTest {
  private static final int REPEAT = 5;

  @Test
  void testPermutationsDiscrete() {
    Random random = new Random();
    Distribution distribution = DiscreteUniformDistribution.of(2, 10);
    for (int n = 9; n < 12; ++n) {
      int ofs = 5 + random.nextInt(10);
      for (int m = ofs; m < ofs + 3; ++m) {
        Tensor matrix = RandomVariate.of(distribution, random, n, m);
        Scalar minimum = BipartiteMatching.of(matrix).minimum();
        for (int index = 0; index < REPEAT; ++index) {
          Tensor tensor = Tensor.of(IntStream.of(RandomPermutation.of(n)).mapToObj(matrix::get));
          BipartiteMatching bipartiteMatching = BipartiteMatching.of(tensor);
          assertEquals(minimum, bipartiteMatching.minimum());
        }
        for (int index = 0; index < REPEAT; ++index) {
          int[] perm = RandomPermutation.of(m);
          Tensor tensor = Tensor.of(matrix.stream().map(row -> Tensor.of(IntStream.of(perm).mapToObj(row::Get))));
          BipartiteMatching bipartiteMatching = BipartiteMatching.of(tensor);
          assertEquals(minimum, bipartiteMatching.minimum());
        }
      }
    }
  }

  @Test
  void testPermutationsContinuous() {
    Random random = new Random();
    Distribution distribution = UniformDistribution.of(0, 1);
    for (int n = 9; n < 12; ++n) {
      int ofs = 5 + random.nextInt(10);
      for (int m = ofs; m < ofs + 3; ++m) {
        Tensor matrix = RandomVariate.of(distribution, random, n, m);
        BipartiteMatching bm = BipartiteMatching.of(matrix);
        Scalar minimum = bm.minimum();
        int[] solution = bm.matching();
        for (int index = 0; index < REPEAT; ++index) {
          int[] perm = RandomPermutation.of(n);
          Tensor tensor = Tensor.of(IntStream.of(perm).mapToObj(matrix::get));
          BipartiteMatching bipartiteMatching = BipartiteMatching.of(tensor);
          Tolerance.CHOP.requireClose(minimum, bipartiteMatching.minimum());
          assertArrayEquals(IntStream.of(perm).map(i -> solution[i]).toArray(), bipartiteMatching.matching());
        }
        for (int index = 0; index < REPEAT; ++index) {
          int[] perm = RandomPermutation.of(m);
          Tensor tensor = Tensor.of(matrix.stream().map(row -> Tensor.of(IntStream.of(perm).mapToObj(row::Get))));
          BipartiteMatching bipartiteMatching = BipartiteMatching.of(tensor);
          Tolerance.CHOP.requireClose(minimum, bipartiteMatching.minimum());
          int[] matching = bipartiteMatching.matching();
          int[] expect = IntStream.range(0, n).map(i -> 0 <= matching[i] ? perm[matching[i]] : -1).toArray();
          assertArrayEquals(expect, solution);
        }
      }
    }
  }

  @Test
  void testPackageVisibility() {
    assertFalse(Modifier.isPublic(BipartitionImpl.class.getModifiers()));
  }
}
