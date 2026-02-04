// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.nrm.VectorInfinityNorm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;

class ComplexDiskUniformDistributionTest {
  @Test
  void test0_1() {
    Distribution distribution = ComplexDiskUniformDistribution.of(0.1);
    assertTrue(distribution.toString().startsWith("ComplexDiskUniformDistribution["));
    Scalar norm = VectorInfinityNorm.of(RandomVariate.of(distribution, 100));
    assertTrue(Scalars.lessEquals(norm, RealScalar.of(0.1)));
  }

  @Test
  void test3() {
    Distribution distribution = ComplexDiskUniformDistribution.of(3);
    Scalar norm = VectorInfinityNorm.of(RandomVariate.of(distribution, 100));
    assertTrue(Scalars.lessEquals(norm, RealScalar.of(3)));
  }

  @Test
  void testSame() {
    Distribution distribution = ComplexDiskUniformDistribution.of(3);
    assertEquals( //
        RandomVariate.of(distribution, new Random(30), 10), //
        RandomVariate.of(distribution, new Random(30), 10));
  }
}
