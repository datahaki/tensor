// code by jph
package ch.alpine.tensor.pdf;

import java.util.function.Function;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ExpectationTest extends TestCase {
  private static void _check(Distribution distribution) {
    Scalar mean = Expectation.mean(distribution);
    {
      Scalar E_X = Expectation.of(Function.identity(), distribution);
      Chop._12.requireClose(E_X, mean);
    }
    {
      Scalar E_X = Expectation.of(s -> s.multiply(RealScalar.of(2)), distribution);
      Chop._12.requireClose(E_X, mean.multiply(RealScalar.of(2)));
    }
    {
      Scalar E_X = Expectation.of(s -> s.multiply(RealScalar.of(2)), distribution);
      Chop._12.requireClose(E_X, mean.multiply(RealScalar.of(2)));
    }
    Scalar var = Expectation.variance(distribution);
    {
      Scalar Var = Expectation.of(s -> AbsSquared.of(s.subtract(mean)), distribution);
      Chop._12.requireClose(Var, var);
    }
  }

  public void testExact() {
    _check(DiscreteUniformDistribution.of(4, 10));
    _check(BernoulliDistribution.of(RationalScalar.of(2, 7)));
    _check(BinomialDistribution.of(10, RationalScalar.of(3, 7)));
    _check(HypergeometricDistribution.of(10, 40, 100));
    _check(EmpiricalDistribution.fromUnscaledPDF(Tensors.vector(3, 2, 1, 4)));
  }

  public void testEmpiricalDistribution() {
    int upper = 200;
    Tensor unscaledPDF = RandomVariate.of(DiscreteUniformDistribution.of(0, 10000), upper);
    Clip clip = Clips.interval(2, 8);
    Distribution distribution = EmpiricalDistribution.fromUnscaledPDF(unscaledPDF);
    ScalarUnaryOperator suo = clip;
    Scalar expect = Expectation.of(suo, distribution);
    Tensor accumulate = Accumulate.of(unscaledPDF);
    Scalar scale = Last.of(accumulate);
    Tensor pdf = unscaledPDF.divide(scale);
    Scalar result = (Scalar) pdf.dot(Range.of(0, upper).map(suo));
    assertEquals(expect, result);
    ExactScalarQ.require(expect);
    ExactScalarQ.require(result);
    Scalar variance = Expectation.variance(distribution);
    ExactScalarQ.require(variance);
    double varDouble = variance.number().doubleValue();
    assertTrue(2500 < varDouble);
    assertTrue(varDouble < 4500);
  }

  public void testNumeric() {
    _check(PoissonDistribution.of(RationalScalar.of(4, 3)));
    _check(GeometricDistribution.of(RealScalar.of(0.3)));
    _check(EmpiricalDistribution.fromUnscaledPDF(Tensors.vector(3, 0.2, 1, 0.4)));
  }

  public void testExpectationDistribution() {
    Distribution distribution = new SingletonDistribution(13);
    assertEquals(Expectation.mean(distribution), RealScalar.of(13));
    assertEquals(Expectation.variance(distribution), RealScalar.of(0));
  }

  public void testFail() {
    AssertFail.of(() -> Expectation.of(s -> s, NormalDistribution.standard()));
  }

  public void testFail2() {
    Distribution distribution = GompertzMakehamDistribution.of(RealScalar.of(1), RealScalar.of(2));
    AssertFail.of(() -> Expectation.mean(distribution));
  }
}
