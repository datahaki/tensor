// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.c.BirnbaumSaundersDistribution;
import ch.alpine.tensor.pdf.c.DiracDeltaDistribution;
import ch.alpine.tensor.pdf.c.ErlangDistribution;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.pdf.c.FRatioDistribution;
import ch.alpine.tensor.pdf.c.FisherZDistribution;
import ch.alpine.tensor.pdf.c.GammaDistribution;
import ch.alpine.tensor.pdf.c.HistogramDistribution;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.c.MaxwellDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.StudentTDistribution;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.pdf.d.GeometricDistribution;
import ch.alpine.tensor.pdf.d.PoissonDistribution;

class RandomVariateTest {
  @Test
  void testVarying() {
    Distribution distribution = NormalDistribution.standard();
    Set<Tensor> set = RandomVariate.of(distribution, 1000).stream().collect(Collectors.toSet());
    assertTrue(970 < set.size());
    RandomVariate.of(distribution, 2, 3, 4);
  }

  private static Distribution[] distribs() {
    return new Distribution[] { //
        UniformDistribution.unit(), //
        UniformDistribution.of(2, 3), //
        GeometricDistribution.of(0.3), //
        DiracDeltaDistribution.of(Pi.VALUE), //
        PoissonDistribution.of(0.3), //
        UniformDistribution.unit(4), //
        BirnbaumSaundersDistribution.of(1.1, 2.2), //
        MaxwellDistribution.of(1.3), //
        FRatioDistribution.of(1.3, 1.7), //
        FisherZDistribution.of(1.1, 2.2), //
        ExponentialDistribution.of(.4), //
        BinomialDistribution.of(3, .4), //
        LogNormalDistribution.standard(), //
        ErlangDistribution.of(3, 1.2), //
        GammaDistribution.of(1.4, 2.3), //
        StudentTDistribution.of(1.1, 1, 3.3), //
        HistogramDistribution.of(Tensors.vector(1, 2, 3, 4, 1.3, 2.2, 1, 4.4)), //
        TriangularDistribution.of(2, 3, 5), //
        NormalDistribution.of(2, 3), //
        NormalDistribution.standard(), //
    };
  }

  @ParameterizedTest
  @MethodSource("distribs")
  void testSame(Distribution distribution) {
    assertEquals( //
        RandomVariate.of(distribution, new Random(10), 30), //
        RandomVariate.of(distribution, new Random(10), 30) //
    );
  }

  @Test
  void testFormatArray() {
    Distribution distribution = DiscreteUniformDistribution.of(2, 11);
    Tensor array = RandomVariate.of(distribution, 3, 4, 5);
    assertEquals(Dimensions.of(array), Arrays.asList(3, 4, 5));
  }

  @Test
  void testFormatList() {
    Distribution distribution = DiscreteUniformDistribution.of(2, 11);
    List<Integer> list = Arrays.asList(3, 4, 5);
    Tensor array = RandomVariate.of(distribution, list);
    assertEquals(Dimensions.of(array), list);
  }

  @Test
  void testFormatList1() {
    Distribution distribution = BinomialDistribution.of(3, RationalScalar.of(1, 2));
    Tensor array = RandomVariate.of(distribution, 1);
    assertEquals(Dimensions.of(array), List.of(1));
    assertFalse(array instanceof Scalar);
  }
}
