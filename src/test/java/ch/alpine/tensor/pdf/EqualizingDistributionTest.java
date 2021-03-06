// code by jph
package ch.alpine.tensor.pdf;

import java.io.IOException;
import java.util.Collections;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Differences;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class EqualizingDistributionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(EqualizingDistribution.fromUnscaledPDF(Tensors.vector(3)));
    CDF cdf = CDF.of(distribution);
    Tensor domain = Subdivide.of(0, 1, 10);
    assertEquals(domain, domain.map(cdf::p_lessThan));
    assertEquals(domain, domain.map(cdf::p_lessEquals));
    PDF pdf = PDF.of(distribution);
    assertEquals(domain.map(pdf::at), Tensors.vector(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0));
    assertEquals(Mean.of(distribution), RationalScalar.HALF);
    assertEquals(Variance.of(distribution), RationalScalar.of(1, 12));
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    assertEquals(domain.map(inverseCDF::quantile), domain);
    RandomVariate.of(distribution, 30).map(Clips.unit()::requireInside);
  }

  public void testResample() {
    Tensor vector = Tensors.vector(-3, 6, 10, 20, 22, 30);
    Distribution distribution = EqualizingDistribution.fromUnscaledPDF(Differences.of(vector));
    Tensor domain = Subdivide.of(0, 1, 10);
    InverseCDF inverseCDF = InverseCDF.of(distribution);
    Tensor tensor = domain.map(inverseCDF::quantile);
    Tensor linear = tensor.map(LinearInterpolation.of(vector)::At);
    assertEquals(linear.Get(0), RealScalar.of(-3));
    assertEquals(Last.of(linear), RealScalar.of(30));
    Tensor uniform = Differences.of(linear);
    ExactTensorQ.require(uniform);
    assertEquals(Tally.of(uniform), Collections.singletonMap(RationalScalar.of(33, 10), 10L));
    assertTrue(distribution.toString().startsWith("EqualizingDistribution"));
  }

  public void testNegativeFail() {
    AssertFail.of(() -> EqualizingDistribution.fromUnscaledPDF(Tensors.vector(0, -9, 1)));
  }

  public void testZeroFail() {
    AssertFail.of(() -> EqualizingDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 0)));
  }

  public void testEmptyFail() {
    AssertFail.of(() -> EqualizingDistribution.fromUnscaledPDF(Tensors.empty()));
  }

  public void testScalarFail() {
    AssertFail.of(() -> EqualizingDistribution.fromUnscaledPDF(RealScalar.ONE));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> EqualizingDistribution.fromUnscaledPDF(HilbertMatrix.of(10)));
  }
}
