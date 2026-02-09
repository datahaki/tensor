// code by jph
package ch.alpine.tensor.sca.erf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.sca.Chop;

class ErfcTest {
  @Test
  void testCompare() {
    Tensor x = Subdivide.of(-0.6, 0.6, 50);
    Chop._07.requireClose(x.maps(Erfc.FUNCTION), x.maps(ErfcRestricted.FUNCTION));
  }

  @Test
  void testLimits() {
    assertEquals(Erfc.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY), RealScalar.ZERO);
    assertEquals(Erfc.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY), RealScalar.of(2));
    Chop._06.requireClose(Erfc.FUNCTION.apply(RealScalar.ZERO), RealScalar.ONE);
  }

  @Test
  void testComplex() {
    Scalar scalar = ComplexScalar.of(1.2, 1.4);
    Scalar expect = ComplexScalar.of(-0.29466994521574197, 0.4089868112498779); // Mathematica
    Scalar result = Erfc.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(expect, result);
  }

  @Test
  void testComplexNegative() {
    Scalar scalar = ComplexScalar.of(-1.2, 1.4);
    Scalar expect = ComplexScalar.of(2.294669945215742, 0.4089868112498779); // Mathematica
    Scalar result = Erfc.FUNCTION.apply(scalar);
    Tolerance.CHOP.requireClose(expect, result);
  }

  @Test
  void testFail() {
    assertThrows(Throw.class, () -> ErfcRestricted.FUNCTION.apply(GaussScalar.of(6, 7)));
  }
}
