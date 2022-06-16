// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.CDF;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;

class MaxwellDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(MaxwellDistribution.of(1.3));
    Tolerance.CHOP.requireClose( //
        PDF.of(distribution).at(RealScalar.of(0.7)), //
        RealScalar.of(0.15393813960787467));
    assertEquals(PDF.of(distribution).at(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(PDF.of(distribution).at(RealScalar.of(-1)), RealScalar.of(0));
    Tolerance.CHOP.requireClose( //
        CDF.of(distribution).p_lessEquals(RealScalar.of(0.7)), //
        RealScalar.of(0.03809089734621629));
    Tolerance.CHOP.requireClose(CDF.of(distribution).p_lessEquals(RealScalar.of(0)), RealScalar.of(0));
    assertEquals(CDF.of(distribution).p_lessEquals(RealScalar.of(-1)), RealScalar.of(0));
    Tolerance.CHOP.requireClose( //
        Mean.of(distribution), //
        RealScalar.of(2.0744998580874503));
    Tolerance.CHOP.requireClose( //
        Variance.of(distribution), //
        RealScalar.of(0.76645033879515));
    assertTrue(distribution.toString().startsWith("MaxwellDistribution["));
  }

  @Test
  void testMarkov() {
    Random random = new Random();
    Distribution distribution = MaxwellDistribution.of(0.1 + random.nextDouble());
    TestMarkovChebyshev.markov(distribution);
    TestMarkovChebyshev.chebyshev(distribution);
  }

  @Test
  void testSigmaFail() {
    assertThrows(TensorRuntimeException.class, () -> MaxwellDistribution.of(0));
    assertThrows(TensorRuntimeException.class, () -> MaxwellDistribution.of(-1));
    assertThrows(TensorRuntimeException.class, () -> MaxwellDistribution.of(Quantity.of(2, "m")));
  }
}
