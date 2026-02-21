// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.jet.JetScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Clips;
import test.wrap.SerializableQ;

class ArcSinDistributionTest {
  @Test
  void testConsistent() {
    Distribution d = ArcSinDistribution.INSTANCE;
    PDF pdf = PDF.of(d);
    CDF cdf = CDF.of(d);
    InverseCDF inv = InverseCDF.of(d);
    Distribution uni = UniformDistribution.of(Clips.absoluteOne());
    Scalar x0 = RandomVariate.of(uni);
    Scalar p = cdf.p_lessThan(x0);
    Scalar x1 = inv.quantile(p);
    Tolerance.CHOP.requireClose(x0, x1);
    Scalar x = JetScalar.of(Rational.HALF, 4);
    JetScalar js1 = (JetScalar) cdf.p_lessEquals(x);
    JetScalar js2 = (JetScalar) pdf.at(x);
    Tolerance.CHOP.requireClose(js1.vector().extract(1, 4), js2.vector().extract(0, 3));
    assertTrue(d.toString().startsWith("ArcSinDistribution"));
  }

  @Test
  void testSerializable() {
    SerializableQ.require(ArcSinDistribution.INSTANCE);
  }
}
