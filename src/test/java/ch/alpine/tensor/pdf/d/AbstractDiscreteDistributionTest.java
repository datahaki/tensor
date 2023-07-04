// code by jph
package ch.alpine.tensor.pdf.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.InverseCDF;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.TransformedDistribution;

class AbstractDiscreteDistributionTest {
  static void _checkSame(Distribution d1, Distribution d2) {
    {
      Tensor tensor = Range.of(0, 10);
      assertEquals(tensor.map(PDF.of(d1)::at), tensor.map(PDF.of(d2)::at));
      assertEquals(tensor.map(CDF.of(d1)::p_lessEquals), tensor.map(CDF.of(d2)::p_lessEquals));
      assertEquals(tensor.map(CDF.of(d1)::p_lessThan), tensor.map(CDF.of(d2)::p_lessThan));
    }
    for (int n = 1; n < 10; ++n) {
      Tensor tensor = Subdivide.of(0, 1, n);
      Tensor q1 = tensor.map(InverseCDF.of(d1)::quantile);
      Tensor q2 = tensor.map(InverseCDF.of(d2)::quantile);
      assertEquals(q1, q2);
    }
  }

  @Test
  void testBinomial() {
    Distribution d1 = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(1, 3, 3, 1));
    Distribution d2 = BinomialDistribution.of(3, RationalScalar.HALF);
    _checkSame(d1, d2);
  }

  @Test
  void testBinomialShifted() {
    Distribution d1 = CategoricalDistribution.fromUnscaledPDF(Tensors.vector(0, 0, 1, 3, 3, 1));
    Distribution d2 = TransformedDistribution.shift(BinomialDistribution.of(3, RationalScalar.HALF), RealScalar.TWO);
    _checkSame(d1, d2);
  }

  @RepeatedTest(5)
  void testUniform(RepetitionInfo repetitionInfo) {
    int max = repetitionInfo.getCurrentRepetition();
    Distribution d1 = CategoricalDistribution.fromUnscaledPDF(ConstantArray.of(RealScalar.ONE, max));
    Distribution d2 = DiscreteUniformDistribution.of(0, max);
    _checkSame(d1, d2);
  }

  @Test
  void testVisibility() {
    assertTrue(Modifier.isPublic(AbstractDiscreteDistribution.class.getModifiers()));
  }
}
