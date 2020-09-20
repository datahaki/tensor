// code by jph
package ch.ethz.idsc.tensor.opt.hun;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class HungarianAlgorithmTest extends TestCase {
  private static void _verify(Tensor matrix, Scalar minimum, Tensor expected) {
    BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
    Tolerance.CHOP.requireClose(bipartiteMatching.minimum(), minimum);
    if (!ExactScalarQ.of(bipartiteMatching.minimum()))
      assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), expected);
  }

  private static void _check1(Tensor matrix, Scalar minimum, Tensor expected) {
    _verify(matrix, minimum, expected);
    _check2(matrix, minimum, expected);
  }

  private static void _check2(Tensor matrix, Scalar minimum, Tensor expected) {
    Tensor cost = Transpose.of(matrix);
    int[] perm = new int[cost.length()];
    Arrays.fill(perm, BipartiteMatching.UNASSIGNED);
    for (int index = 0; index < expected.length(); ++index) {
      int ordinal = expected.Get(index).number().intValue();
      if (0 <= ordinal)
        perm[ordinal] = index;
    }
    _verify(cost, minimum, Tensors.vectorInt(perm));
  }

  public void testExample1() {
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 3, 6, 2, 1, 8 }, //
        { 1, 5, 4, 6, 8, 6 }, //
        { 2, 15, 1, 2, 9, 2 }, //
        { 3, 4, 2, 5, 4, 4 }, //
        { 4, 2, 1, 5, 6, 3 } });
    _check1(matrix, RealScalar.of(8), Tensors.vector(4, 0, 3, 2, 1));
  }

  public void testExample2() {
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 8, 2, 3, 6, 2, 1, 8 }, //
        { 6, 1, 5, 4, 6, 8, 6 }, //
        { 2, 2, 15, 1, 2, 9, 2 }, //
        { 4, 3, 4, 2, 5, 4, 4 }, //
        { 3, 4, 2, 1, 5, 6, 3 } });
    _check1(matrix, RealScalar.of(8), Tensors.vector(5, 1, 4, 3, 2));
  }

  public void testExample3() {
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 2, 3, 2 }, //
        { 1, 5, 4 }, //
        { 20, 15, 11 }, //
        { 10, 15, 11 }, //
        { 5, 1, 3 } });
    _check1(matrix, RealScalar.of(4), Tensors.vector(2, 0, -1, -1, 1));
  }

  public void testExample4() {
    Tensor matrix = Tensors.matrixInt(new int[][] { //
        { 8, 2, 3, 6, 2 }, //
        { 6, 1, 5, 4, 6 }, //
        { 2, 2, 15, 1, 2 }, //
        { 4, 3, 4, 2, 5 }, //
        { 3, 4, 2, 1, 5 } });
    _check1(matrix, RealScalar.of(9), Tensors.vector(4, 1, 0, 3, 2));
  }

  private static void _check3(Tensor matrix) throws ClassNotFoundException, IOException {
    BipartiteMatching bipartiteMatching = Serialization.copy(BipartiteMatching.of(matrix));
    _check2(matrix, bipartiteMatching.minimum(), Tensors.vectorInt(bipartiteMatching.matching()));
    HungarianAlgorithm hungarianAlgorithm = (HungarianAlgorithm) bipartiteMatching;
    List<Integer> list = Dimensions.of(matrix);
    assertEquals(bipartiteMatching.matching().length, matrix.length());
    assertTrue(hungarianAlgorithm.iterations() <= Math.min(list.get(0), list.get(1)));
  }

  private static final Random RANDOM = new Random();

  public void testRandom() throws ClassNotFoundException, IOException {
    Distribution distribution = UniformDistribution.unit();
    for (int count = 0; count < 10; ++count) {
      int row = 1 + RANDOM.nextInt(100);
      _check3(RandomVariate.of(distribution, row, 53));
      _check3(RandomVariate.of(distribution, 53, row));
    }
  }

  public void testDiscreteRandom() throws ClassNotFoundException, IOException {
    Distribution distribution = DiscreteUniformDistribution.of(2, 100);
    for (int count = 0; count < 10; ++count) {
      int row = 1 + RANDOM.nextInt(100);
      _check3(RandomVariate.of(distribution, row, 53));
      _check3(RandomVariate.of(distribution, 53, row));
    }
  }
}
