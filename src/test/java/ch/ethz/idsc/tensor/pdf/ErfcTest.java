// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.num.GaussScalar;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ErfcTest extends TestCase {
  public void testCompare() {
    Tensor x = Subdivide.of(-0.6, 0.6, 50);
    assertTrue(Chop._07.close(Erfc.of(x), x.map(ErfcRestricted.FUNCTION)));
  }

  public void testLimits() {
    assertEquals(Erfc.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY), RealScalar.ZERO);
    assertEquals(Erfc.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY), RealScalar.of(2));
    Chop._06.requireClose(Erfc.FUNCTION.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  public void testComplex() {
    Scalar scalar = ComplexScalar.of(1.2, 1.4);
    Scalar expect = ComplexScalar.of(-0.29466994521574197, 0.4089868112498779); // Mathematica
    Scalar result = Erfc.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(expect, result);
  }

  public void testComplexNegative() {
    Scalar scalar = ComplexScalar.of(-1.2, 1.4);
    Scalar expect = ComplexScalar.of(2.294669945215742, 0.4089868112498779); // Mathematica
    Scalar result = Erfc.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(expect, result);
  }

  public void testFail() {
    try {
      ErfcRestricted.FUNCTION.apply(GaussScalar.of(6, 7));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
