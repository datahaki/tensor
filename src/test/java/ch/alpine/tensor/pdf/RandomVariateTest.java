// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class RandomVariateTest {
  @Test
  void testVarying() {
    Distribution distribution = NormalDistribution.standard();
    Set<Tensor> set = RandomVariate.of(distribution, 1000).stream().collect(Collectors.toSet());
    assertTrue(970 < set.size());
  }

  @Test
  void testSame() {
    Distribution distribution = NormalDistribution.standard();
    assertEquals( //
        RandomVariate.of(distribution, new Random(10), 1000), //
        RandomVariate.of(distribution, new Random(10), 1000) //
    );
    RandomVariate.of(distribution, new SecureRandom(), 2, 3, 4);
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
    assertEquals(Dimensions.of(array), Arrays.asList(1));
    assertFalse(array instanceof Scalar);
  }
}
