// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.red.Quantile;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class TruncatedDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Clip clip = Clips.interval(10, 11);
    Distribution distribution = Serialization.copy(TruncatedDistribution.of(NormalDistribution.of(10, 2), clip));
    Scalar scalar = RandomVariate.of(distribution);
    assertTrue(clip.isInside(scalar));
  }

  public void testInfinite() {
    Clip clip = Clips.interval(0, Double.POSITIVE_INFINITY);
    Distribution distribution = TruncatedDistribution.of(NormalDistribution.of(0, 1), clip);
    assertEquals(PDF.of(distribution).at(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(PDF.of(distribution).at(RealScalar.of(1)), //
        PDF.of(NormalDistribution.of(0, 1)).at(RealScalar.ONE).multiply(RealScalar.of(2)));
    Sign.requirePositiveOrZero(RandomVariate.of(distribution));
    Tolerance.CHOP.requireZero(Quantile.of(distribution).apply(RealScalar.ZERO));
    CDF cdf = CDF.of(distribution);
    Tolerance.CHOP.requireZero(cdf.p_lessEquals(RealScalar.ZERO));
    Tolerance.CHOP.requireZero(cdf.p_lessThan(RealScalar.ZERO));
  }

  public void testSerializable() throws ClassNotFoundException, IOException {
    Clip clip = Clips.interval(10, 11);
    Distribution distribution = TruncatedDistribution.of(BinomialDistribution.of(20, DoubleScalar.of(0.5)), clip);
    Scalar scalar = RandomVariate.of(distribution);
    ExactScalarQ.require(scalar);
    assertTrue(clip.isInside(scalar));
    Serialization.copy(distribution);
    Serialization.copy(TruncatedDistribution.of(NormalDistribution.of(10, 2), clip));
  }

  public void testFail() {
    Clip clip = Clips.interval(10, 11);
    AssertFail.of(() -> TruncatedDistribution.of(NormalDistribution.of(-100, 0.2), clip));
  }

  public void testNullFail() {
    AssertFail.of(() -> TruncatedDistribution.of(NormalDistribution.of(-100, 0.2), null));
    AssertFail.of(() -> TruncatedDistribution.of(null, Clips.interval(10, 11)));
  }
}
