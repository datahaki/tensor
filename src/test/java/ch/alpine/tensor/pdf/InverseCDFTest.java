// code by jph
package ch.alpine.tensor.pdf;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.c.ArcSinDistribution;
import ch.alpine.tensor.pdf.c.BetaDistribution;
import ch.alpine.tensor.pdf.c.BirnbaumSaundersDistribution;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.ErlangDistribution;
import ch.alpine.tensor.pdf.c.FisherZDistribution;
import ch.alpine.tensor.pdf.c.GompertzMakehamDistribution;
import ch.alpine.tensor.pdf.c.GumbelDistribution;
import ch.alpine.tensor.pdf.c.HoytDistribution;
import ch.alpine.tensor.pdf.c.KDistribution;
import ch.alpine.tensor.pdf.c.LaplaceDistribution;
import ch.alpine.tensor.pdf.c.LevyDistribution;
import ch.alpine.tensor.pdf.c.LogisticDistribution;
import ch.alpine.tensor.pdf.c.NakagamiDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.ParetoDistribution;
import ch.alpine.tensor.pdf.c.RayleighDistribution;
import ch.alpine.tensor.pdf.c.RiceDistribution;
import ch.alpine.tensor.pdf.c.StudentTDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.pdf.c.WeibullDistribution;
import ch.alpine.tensor.sca.Chop;

class InverseCDFTest {
  static final Distribution[] DISTRIBS = { //
      ArcSinDistribution.INSTANCE, //
      BetaDistribution.of(0.2, 0.3), //
      BirnbaumSaundersDistribution.of(3.2, 1.3), //
      CauchyDistribution.of(-1.2, 3.2), //
      ErlangDistribution.of(1, 0.3), //
      ErlangDistribution.of(1, 1.6), //
      FisherZDistribution.of(1.2, 2.3), //
      GompertzMakehamDistribution.of(2.3, 3.4), //
      GumbelDistribution.of(-0.2, 0.3), //
      HoytDistribution.of(0.6, 1.9), //
      KDistribution.of(1, 0.8), //
      KDistribution.of(2, 0.8), //
      LaplaceDistribution.of(-2.3, 2), //
      LevyDistribution.of(-2.3, 1.2), //
      LogisticDistribution.of(-1.2, 2), //
      // TODO
      // MaxwellDistribution.of(0.4), //
      // MaxwellDistribution.of(1.4), //
      NakagamiDistribution.of(0.3, 0.4), //
      NormalDistribution.standard(), //
      ParetoDistribution.of(1.2, 0.3), //
      RayleighDistribution.of(0.2), //
      RiceDistribution.of(0.3, 1.2), //
      StudentTDistribution.of(-2.1, 1.1, 1.1), //
      WeibullDistribution.of(0.2, 0.3), //
  };

  static Stream<Distribution> distributions() {
    return Stream.of(DISTRIBS);
  }

  @ParameterizedTest
  @MethodSource("distributions")
  void testInverseCDF(Distribution distribution) {
    assumeTrue(distribution instanceof InverseCDF);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    inverseCDF.quantile(RealScalar.ZERO);
    inverseCDF.quantile(RealScalar.ONE);
    Scalar p = RandomVariate.of(UniformDistribution.unit());
    Scalar x = inverseCDF.quantile(p);
    CDF cdf = CDF.of(distribution);
    Scalar p_lessEquals = cdf.p_lessEquals(x);
    Chop._10.requireClose(p, p_lessEquals);
  }
}
