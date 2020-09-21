// code by jph
package ch.ethz.idsc.tensor.opt.hun;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class BipartiteMatchingTest extends TestCase {
  private static final int MAX = 7;

  public void testSquare() {
    Distribution distribution = DiscreteUniformDistribution.of(2, 50);
    for (int n = 1; n < MAX; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n);
      for (int index = 0; index < n; ++index)
        matrix.set(RealScalar.ONE, index, index);
      {
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
        assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), Range.of(0, n));
      }
      for (int count = 0; count < n; ++count) {
        List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        Tensor mtx = Tensor.of(list.stream().map(matrix::get));
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(mtx);
        assertEquals(Tensors.vector(list), Tensors.vectorInt(bipartiteMatching.matching()));
        assertEquals(ExactScalarQ.require(bipartiteMatching.minimum()), RealScalar.of(n));
      }
    }
  }

  public void testSquareNumeric() {
    Distribution distribution = UniformDistribution.of(2, 3);
    for (int n = 1; n < MAX; ++n) {
      Tensor matrix = RandomVariate.of(distribution, n, n);
      for (int index = 0; index < n; ++index)
        matrix.set(RealScalar.ONE, index, index);
      {
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
        assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), Range.of(0, n));
      }
      for (int count = 0; count < n; ++count) {
        List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        Tensor mtx = Tensor.of(list.stream().map(matrix::get));
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(mtx);
        assertEquals(Tensors.vector(list), Tensors.vectorInt(bipartiteMatching.matching()));
        Tolerance.CHOP.requireClose(bipartiteMatching.minimum(), RealScalar.of(n));
      }
    }
  }

  public void testRectangle1() {
    Distribution distribution = DiscreteUniformDistribution.of(2, 50);
    for (int n = 4; n < MAX; ++n) {
      int m = n - 3;
      Tensor matrix = RandomVariate.of(distribution, m, n);
      for (int index = 0; index < m; ++index)
        matrix.set(RealScalar.ONE, index, index);
      {
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
        assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), Range.of(0, m));
      }
      for (int count = 0; count < m; ++count) {
        List<Integer> list = IntStream.range(0, m).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        Tensor shuffle = Tensor.of(list.stream().map(matrix::get));
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(shuffle);
        assertEquals(Tensors.vector(list), Tensors.vectorInt(bipartiteMatching.matching()));
        assertEquals(ExactScalarQ.require(bipartiteMatching.minimum()), RealScalar.of(m));
      }
    }
  }

  public void testRectangle1Numeric() {
    Distribution distribution = UniformDistribution.of(2, 3);
    for (int n = 4; n < MAX; ++n) {
      int m = n - 3;
      Tensor matrix = RandomVariate.of(distribution, m, n);
      for (int index = 0; index < m; ++index)
        matrix.set(RealScalar.of(1.0), index, index);
      {
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
        assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), Range.of(0, m));
      }
      for (int count = 0; count < m; ++count) {
        List<Integer> list = IntStream.range(0, m).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        Tensor shuffle = Tensor.of(list.stream().map(matrix::get));
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(shuffle);
        assertEquals(Tensors.vector(list), Tensors.vectorInt(bipartiteMatching.matching()));
        Tolerance.CHOP.requireClose(bipartiteMatching.minimum(), RealScalar.of(m));
      }
    }
  }

  public void testRactangle2() {
    Distribution distribution = DiscreteUniformDistribution.of(2, 50);
    for (int n = 4; n < MAX; ++n) {
      int m = n - 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      for (int index = 0; index < m; ++index)
        matrix.set(RealScalar.ONE, index, index);
      Tensor range = Join.of(Range.of(0, m), ConstantArray.of(RealScalar.of(BipartiteMatching.UNASSIGNED), n - m));
      {
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
        assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), range);
      }
      for (int count = 0; count < n; ++count) {
        List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        Tensor shuffle = Tensor.of(list.stream().map(matrix::get));
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(shuffle);
        assertEquals(Tensor.of(list.stream().map(range::get)), Tensors.vectorInt(bipartiteMatching.matching()));
        assertEquals(ExactScalarQ.require(bipartiteMatching.minimum()), RealScalar.of(m));
      }
    }
  }

  public void testRactangle2Numeric() {
    Distribution distribution = UniformDistribution.of(2, 3);
    for (int n = 4; n < MAX; ++n) {
      int m = n - 3;
      Tensor matrix = RandomVariate.of(distribution, n, m);
      for (int index = 0; index < m; ++index)
        matrix.set(RealScalar.of(1.0), index, index);
      Tensor range = Join.of(Range.of(0, m), ConstantArray.of(RealScalar.of(BipartiteMatching.UNASSIGNED), n - m));
      {
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
        assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), range);
      }
      for (int count = 0; count < n; ++count) {
        List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        Tensor shuffle = Tensor.of(list.stream().map(matrix::get));
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(shuffle);
        assertEquals(Tensor.of(list.stream().map(range::get)), Tensors.vectorInt(bipartiteMatching.matching()));
        Tolerance.CHOP.requireClose(bipartiteMatching.minimum(), RealScalar.of(m));
      }
    }
  }

  public void testNegative() {
    Distribution distribution = DiscreteUniformDistribution.of(-50, 50);
    for (int n = 1; n < MAX; ++n) {
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(RandomVariate.of(distribution, n, n));
      ExactScalarQ.require(bipartiteMatching.minimum());
    }
    for (int n = 1; n < MAX; ++n) {
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(RandomVariate.of(distribution, n + 2, n));
      ExactScalarQ.require(bipartiteMatching.minimum());
    }
    for (int n = 1; n < MAX; ++n) {
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(RandomVariate.of(distribution, n, n + 2));
      ExactScalarQ.require(bipartiteMatching.minimum());
    }
  }

  public void testScalarFail() {
    try {
      BipartiteMatching.of(Pi.VALUE);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testEmptyFail() {
    try {
      BipartiteMatching.of(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
