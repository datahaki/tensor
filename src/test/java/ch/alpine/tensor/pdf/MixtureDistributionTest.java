// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.sca.Sign;

class MixtureDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution d = BernoulliDistribution.of(Rational.HALF);
    Distribution d1 = Serialization.copy(MixtureDistribution.of(Tensors.vector(1, 2, 3), d, d, d));
    Distribution d2 = BernoulliDistribution.of(Rational.HALF);
    Tensor domain = Range.of(-1, 3);
    assertEquals(domain.maps(PDF.of(d1)::at), domain.maps(PDF.of(d2)::at));
  }

  @Test
  void testNormal() {
    Distribution d1 = MixtureDistribution.of(Tensors.vector(1, 2, 3), //
        NormalDistribution.of(0, 1), //
        NormalDistribution.of(3, 1), //
        NormalDistribution.of(10, 1));
    assertEquals(Mean.of(d1), RealScalar.of(6));
    Scalar r1 = RandomVariate.of(d1, new Random(1));
    Scalar r2 = RandomVariate.of(d1, new Random(1));
    assertEquals(r1, r2);
    CDF cdf = CDF.of(d1);
    Scalar x = RealScalar.of(1.2);
    Tolerance.CHOP.requireClose(cdf.p_lessEquals(x), cdf.p_lessThan(x));
    Scalar scalar = PDF.of(d1).at(RealScalar.of(1));
    Sign.requirePositive(scalar);
  }

  @Test
  void testMixDiscreteCont() {
    Distribution distribution = MixtureDistribution.of(Tensors.vector(1, 1), //
        NormalDistribution.of(0, 1), //
        BernoulliDistribution.of(Rational.HALF));
    Tensor tensor = RandomVariate.of(distribution, 1000);
    Map<Scalar, Long> map = Tally.of(tensor.stream().map(Scalar.class::cast).filter(ExactScalarQ::of));
    assertTrue(150 < map.get(RealScalar.ZERO));
    assertTrue(150 < map.get(RealScalar.ONE));
  }

  @Test
  void testFailNegative() {
    assertThrows(Throw.class, () -> MixtureDistribution.of(Tensors.vector(1, -2, 3), //
        NormalDistribution.of(0, 1), //
        NormalDistribution.of(3, 1), //
        NormalDistribution.of(10, 1)));
  }

  @Test
  void testFailLength() {
    assertThrows(IllegalArgumentException.class, () -> MixtureDistribution.of(Tensors.vector(1, 3), //
        NormalDistribution.of(0, 1), //
        NormalDistribution.of(3, 1), //
        NormalDistribution.of(10, 1)));
  }

  @Test
  void testFailEmpty() {
    assertThrows(Exception.class, () -> MixtureDistribution.of(Tensors.empty()));
    assertThrows(Exception.class, () -> MixtureDistribution.of(Tensors.empty(), List.of()));
  }
}
