// code by jph
package ch.alpine.tensor.opt.hun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Round;

class BipartiteMatchingTest {
  private static final int MAX = 7;

  @Test
  void testExactPrecision() {
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

  @Test
  void testNumericPrecision() {
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

  @Test
  void testNegative() {
    Distribution distribution = DiscreteUniformDistribution.of(-50, 50);
    for (int rows = 1; rows < MAX; ++rows)
      for (int cols = 1; cols < MAX; ++cols) {
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(RandomVariate.of(distribution, rows, cols));
        ExactScalarQ.require(bipartiteMatching.minimum());
      }
  }

  @Test
  void testNegativeWithUnits() {
    Distribution distribution = UniformDistribution.of(Quantity.of(-9, "MYR"), Quantity.of(10, "MYR"));
    for (int rows = 1; rows < MAX; ++rows)
      for (int cols = 1; cols < MAX; ++cols) {
        Tensor tensor = RandomVariate.of(distribution, rows, cols).map(Round.FUNCTION);
        BipartiteMatching bipartiteMatching = BipartiteMatching.of(tensor);
        Scalar min = bipartiteMatching.minimum();
        ExactScalarQ.require(min);
        assertEquals(QuantityUnit.of(min), Unit.of("MYR"));
      }
  }

  @Test
  void testMixedUnitsFail() {
    Tensor matrix = Tensors.matrix(new Scalar[][] { { Quantity.of(1, "MYR"), Quantity.of(1, "SGD") } });
    assertThrows(Exception.class, () -> BipartiteMatching.of(matrix));
  }

  @Test
  void testScalarFail() {
    assertThrows(Exception.class, () -> BipartiteMatching.of(Pi.VALUE));
  }

  @Test
  void testEmptyFail() {
    assertThrows(IllegalArgumentException.class, () -> BipartiteMatching.of(Tensors.empty()));
  }
}
