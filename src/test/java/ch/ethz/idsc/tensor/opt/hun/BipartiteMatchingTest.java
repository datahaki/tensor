// code by jph
package ch.ethz.idsc.tensor.opt.hun;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class BipartiteMatchingTest extends TestCase {
  public void testSquare() {
    int n = 10;
    Distribution distribution = DiscreteUniformDistribution.of(2, 50);
    Tensor matrix = RandomVariate.of(distribution, n, n);
    for (int index = 0; index < n; ++index)
      matrix.set(RealScalar.ONE, index, index);
    {
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
      assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), Range.of(0, n));
    }
    for (int count = 0; count < 10; ++count) {
      List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
      Collections.shuffle(list);
      Tensor mtx = Tensor.of(list.stream().map(matrix::get));
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(mtx);
      assertEquals(Tensors.vector(list), Tensors.vectorInt(bipartiteMatching.matching()));
    }
  }

  public void testRectangle1() {
    int n = 10;
    int m = n - 3;
    Distribution distribution = DiscreteUniformDistribution.of(2, 50);
    Tensor matrix = RandomVariate.of(distribution, m, n);
    for (int index = 0; index < m; ++index)
      matrix.set(RealScalar.ONE, index, index);
    {
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
      assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), Range.of(0, m));
    }
    for (int count = 0; count < 10; ++count) {
      List<Integer> list = IntStream.range(0, m).boxed().collect(Collectors.toList());
      Collections.shuffle(list);
      Tensor shuffle = Tensor.of(list.stream().map(matrix::get));
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(shuffle);
      assertEquals(Tensors.vector(list), Tensors.vectorInt(bipartiteMatching.matching()));
    }
  }

  public void testRactangle2() {
    int n = 10;
    int m = n - 3;
    Distribution distribution = DiscreteUniformDistribution.of(2, 50);
    Tensor matrix = RandomVariate.of(distribution, n, m);
    for (int index = 0; index < m; ++index)
      matrix.set(RealScalar.ONE, index, index);
    Tensor range = Join.of(Range.of(0, m), ConstantArray.of(RealScalar.of(BipartiteMatching.UNASSIGNED), n - m));
    {
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(matrix);
      assertEquals(Tensors.vectorInt(bipartiteMatching.matching()), range);
    }
    for (int count = 0; count < 10; ++count) {
      List<Integer> list = IntStream.range(0, n).boxed().collect(Collectors.toList());
      Collections.shuffle(list);
      Tensor shuffle = Tensor.of(list.stream().map(matrix::get));
      BipartiteMatching bipartiteMatching = BipartiteMatching.of(shuffle);
      assertEquals(Tensor.of(list.stream().map(range::get)), Tensors.vectorInt(bipartiteMatching.matching()));
    }
  }
}
