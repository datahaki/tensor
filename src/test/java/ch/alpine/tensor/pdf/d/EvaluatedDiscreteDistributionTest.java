// code by jph
package ch.alpine.tensor.pdf.d;

import java.util.Map.Entry;
import java.util.NavigableMap;

import ch.alpine.tensor.IntegerQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import junit.framework.TestCase;

public class EvaluatedDiscreteDistributionTest extends TestCase {
  public void testBinomial() {
    for (int n = 10; n < 1200; n += 10) {
      EvaluatedDiscreteDistribution distribution = //
          (EvaluatedDiscreteDistribution) BinomialDistribution.of(n, RealScalar.of(0.333));
      double extreme = Math.nextDown(1.0);
      distribution.quantile(RealScalar.of(extreme));
      NavigableMap<Scalar, Scalar> navigableMap = distribution.inverse_cdf();
      Entry<Scalar, Scalar> entry = navigableMap.lastEntry();
      Chop._12.requireClose(entry.getKey(), RealScalar.ONE);
      IntegerQ.require(entry.getValue());
    }
  }

  public void testBernoulli() {
    Scalar p = RationalScalar.of(1, 3);
    EvaluatedDiscreteDistribution distribution = //
        (EvaluatedDiscreteDistribution) BernoulliDistribution.of(p);
    NavigableMap<Scalar, Scalar> map = distribution.inverse_cdf();
    assertEquals(map.get(RationalScalar.of(2, 3)), RealScalar.ZERO);
    assertEquals(map.get(RationalScalar.of(1, 1)), RealScalar.ONE);
  }

  public void testPoisson() {
    EvaluatedDiscreteDistribution evaluatedDiscreteDistribution = //
        (EvaluatedDiscreteDistribution) PoissonDistribution.of(RealScalar.of(5.5));
    NavigableMap<Scalar, Scalar> navigableMap = evaluatedDiscreteDistribution.inverse_cdf();
    assertTrue(34 < navigableMap.size());
    assertTrue(navigableMap.size() < 38);
    InverseCDF inverseCDF = InverseCDF.of(evaluatedDiscreteDistribution);
    assertTrue(Clips.interval(24, 26).isInside(inverseCDF.quantile(RealScalar.of(0.9999999989237532))));
    assertTrue(Clips.interval(32, 34).isInside(inverseCDF.quantile(RealScalar.of(0.9999999999999985))));
    assertTrue(Clips.interval(1900, 2000).isInside(inverseCDF.quantile(RealScalar.ONE)));
  }
}
