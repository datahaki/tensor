// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.BernoulliDistribution;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Sign;

public class MixtureDistributionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution d = BernoulliDistribution.of(RationalScalar.HALF);
    Distribution d1 = Serialization.copy(MixtureDistribution.of(Tensors.vector(1, 2, 3), d, d, d));
    Distribution d2 = BernoulliDistribution.of(RationalScalar.HALF);
    Tensor domain = Range.of(-1, 3);
    assertEquals(domain.map(PDF.of(d1)::at), domain.map(PDF.of(d2)::at));
  }

  @Test
  public void testNormal() {
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
  public void testFailNegative() {
    assertThrows(TensorRuntimeException.class, () -> MixtureDistribution.of(Tensors.vector(1, -2, 3), //
        NormalDistribution.of(0, 1), //
        NormalDistribution.of(3, 1), //
        NormalDistribution.of(10, 1)));
  }

  @Test
  public void testFailLength() {
    assertThrows(IllegalArgumentException.class, () -> MixtureDistribution.of(Tensors.vector(1, 3), //
        NormalDistribution.of(0, 1), //
        NormalDistribution.of(3, 1), //
        NormalDistribution.of(10, 1)));
  }
}
