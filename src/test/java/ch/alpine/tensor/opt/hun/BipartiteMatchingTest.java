// code by jph
package ch.alpine.tensor.opt.hun;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BipartiteMatchingTest extends TestCase {
  private static final int MAX = 7;

  public void testExactPrecision() {
    Distribution distribution = DiscreteUniformDistribution.of(2, 50);
    for (int rows = 4; rows < MAX; ++rows)
      for (int cols = rows - 3; cols <= rows + 3; ++cols) {
        Tensor matrix = RandomVariate.of(distribution, rows, cols);
        for (int index = 0; index < Math.min(rows, cols); ++index)
          matrix.set(RealScalar.ONE, index, index);
        Tensor range = Join.of( //
            Range.of(0, cols), //
            ConstantArray.of(RealScalar.of(BipartiteMatching.UNASSIGNED), Math.max(0, rows - cols)));
        {
          BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
          if (cols <= rows)
            assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), range);
        }
        for (int count = 0; count < 3; ++count) {
          List<Integer> list = IntStream.range(0, rows).boxed().collect(Collectors.toList());
          Collections.shuffle(list);
          Tensor shuffle = Tensor.of(list.stream().map(matrix::get));
          BipartiteMatching bipartiteMatching = BipartiteMatching.of(shuffle);
          assertEquals(Tensor.of(list.stream().map(range::get)), Tensors.vectorInt(bipartiteMatching.matching()));
          assertEquals(ExactScalarQ.require(bipartiteMatching.minimum()), RealScalar.of(Math.min(rows, cols)));
        }
      }
  }

  public void testNumericPrecision() {
    Distribution distribution = UniformDistribution.of(2, 3);
    for (int rows = 4; rows < MAX; ++rows)
      for (int cols = rows - 3; cols <= rows + 3; ++cols) {
        Tensor matrix = RandomVariate.of(distribution, rows, cols);
        for (int index = 0; index < Math.min(rows, cols); ++index)
          matrix.set(RealScalar.ONE, index, index);
        Tensor range = Join.of( //
            Range.of(0, cols), //
            ConstantArray.of(RealScalar.of(BipartiteMatching.UNASSIGNED), Math.max(0, rows - cols)));
        {
          BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
          if (cols <= rows)
            assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), range);
        }
        for (int count = 0; count < 3; ++count) {
          List<Integer> list = IntStream.range(0, rows).boxed().collect(Collectors.toList());
          Collections.shuffle(list);
          Tensor shuffle = Tensor.of(list.stream().map(matrix::get));
          BipartiteMatching bipartiteMatching = BipartiteMatching.of(shuffle);
          assertEquals(Tensor.of(list.stream().map(range::get)), Tensors.vectorInt(bipartiteMatching.matching()));
          Tolerance.CHOP.requireClose(bipartiteMatching.minimum(), RealScalar.of(Math.min(rows, cols)));
        }
      }
  }

  public void testNegative() {
    Distribution distribution = DiscreteUniformDistribution.of(-50, 50);
    for (int rows = 1; rows < MAX; ++rows)
      for (int cols = 1; cols < MAX; ++cols) {
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(RandomVariate.of(distribution, rows, cols));
        ExactScalarQ.require(bipartiteMatching.minimum());
      }
  }

  public void testScalarFail() {
    AssertFail.of(() -> BipartiteMatching.of(Pi.VALUE));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> BipartiteMatching.of(Tensors.empty()));
  }
}
