// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.pdf.d.BinomialDistribution;
import ch.alpine.tensor.pdf.d.PoissonDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Quantile;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

public class TruncatedDistributionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Clip clip = Clips.interval(10, 11);
    Distribution distribution = Serialization.copy(TruncatedDistribution.of(NormalDistribution.of(10, 2), clip));
    Scalar scalar = RandomVariate.of(distribution);
    assertTrue(clip.isInside(scalar));
  }

  @Test
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

  @Test
  public void testQuantity() {
    Distribution all = TriangularDistribution.with(Quantity.of(10, "m"), Quantity.of(2, "m"));
    Clip clip = Clips.interval(Quantity.of(RationalScalar.of(95, 10), "m"), Quantity.of(12, "m"));
    TruncatedDistribution cut = (TruncatedDistribution) TruncatedDistribution.of(all, clip);
    Clip clip_cdf = cut.clip_cdf();
    clip_cdf.width();
    {
      Scalar x = Quantity.of(RationalScalar.of(0, 10), "m");
      Scalar p1 = PDF.of(all).at(x);
      Scalar p2 = PDF.of(cut).at(x);
      assertEquals(p1, p2);
    }
    {
      Scalar x = Quantity.of(RationalScalar.of(95, 10), "m");
      Scalar p1 = PDF.of(all).at(x);
      Scalar p2 = PDF.of(cut).at(x);
      Tolerance.CHOP.requireClose(p1.divide(clip_cdf.width()), p2);
    }
    {
      Scalar x = Quantity.of(RationalScalar.of(95, 10), "m");
      Scalar p2 = CDF.of(cut).p_lessEquals(x);
      assertTrue(Scalars.isZero(p2));
    }
    {
      Scalar x = Quantity.of(RationalScalar.of(105, 10), "m");
      Scalar p1 = CDF.of(all).p_lessEquals(x);
      Scalar p2 = CDF.of(cut).p_lessEquals(x);
      Tolerance.CHOP.requireClose(clip_cdf.rescale(p1), p2);
    }
    Scalar r = RandomVariate.of(cut);
    Sign.requirePositiveOrZero(r);
    clip.requireInside(r);
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Clip clip = Clips.interval(10, 11);
    Distribution distribution = TruncatedDistribution.of(BinomialDistribution.of(20, DoubleScalar.of(0.5)), clip);
    Scalar scalar = RandomVariate.of(distribution);
    ExactScalarQ.require(scalar);
    assertTrue(clip.isInside(scalar));
    Serialization.copy(distribution);
    Serialization.copy(TruncatedDistribution.of(NormalDistribution.of(10, 2), clip));
  }

  @Test
  public void testDiscrete() {
    Distribution original = PoissonDistribution.of(7);
    Distribution distribution = TruncatedDistribution.of(original, Clips.interval(5, 10));
    CDF cdf = CDF.of(distribution);
    Chop.NONE.requireZero(cdf.p_lessThan(RealScalar.of(2)));
    Chop.NONE.requireZero(cdf.p_lessEquals(RealScalar.of(2)));
    Chop.NONE.requireZero(cdf.p_lessThan(RealScalar.of(5)));
    assertTrue(Scalars.lessThan( //
        PDF.of(original).at(RealScalar.of(5)), //
        PDF.of(distribution).at(RealScalar.of(5))));
  }

  @Test
  public void testToString() {
    Distribution original = PoissonDistribution.of(7);
    Distribution distribution = TruncatedDistribution.of(original, Clips.interval(5, 10));
    assertEquals( //
        distribution.toString(), //
        "TruncatedDistribution[PoissonDistribution[7], Clip[5, 10]]");
  }

  @Test
  public void testFail() {
    Clip clip = Clips.interval(10, 11);
    assertThrows(IllegalArgumentException.class, () -> TruncatedDistribution.of(NormalDistribution.of(-100, 0.2), clip));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> TruncatedDistribution.of(NormalDistribution.of(-100, 0.2), null));
    assertThrows(NullPointerException.class, () -> TruncatedDistribution.of(null, Clips.interval(10, 11)));
  }
}
