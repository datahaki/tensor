// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.sca.pow.Power;

class GaussScalarFailTest {
  @Test
  void testPrimeNegative() {
    assertThrows(ArithmeticException.class, () -> GaussScalar.of(2, -7));
  }

  @Test
  void testPrime() {
    assertThrows(IllegalArgumentException.class, () -> GaussScalar.of(2, 20001));
    assertThrows(IllegalArgumentException.class, () -> GaussScalar.of(2, 100101));
  }

  @Test
  void testIllegalGauss() {
    Scalar a = GaussScalar.of(4, 7);
    Scalar b = GaussScalar.of(4, 11);
    assertThrows(TensorRuntimeException.class, () -> a.add(b));
  }

  @Test
  void testIllegal() {
    Scalar a = GaussScalar.of(4, 7);
    Scalar b = DoubleScalar.of(4.33);
    assertThrows(TensorRuntimeException.class, () -> a.add(b));
    assertThrows(TensorRuntimeException.class, () -> a.multiply(b));
  }

  @Test
  void testMultiplyFail() {
    assertThrows(TensorRuntimeException.class, () -> GaussScalar.of(2, 7).multiply(RealScalar.of(0.3)));
  }

  @Test
  void testPowerFail() {
    Scalar scalar = GaussScalar.of(2, 7);
    assertThrows(TensorRuntimeException.class, () -> Power.of(scalar, 2.3));
    assertThrows(TensorRuntimeException.class, () -> Power.of(scalar, RationalScalar.of(2, 3)));
  }

  @Test
  void testCompareFail1() {
    assertThrows(TensorRuntimeException.class, () -> Scalars.compare(GaussScalar.of(2, 7), GaussScalar.of(9, 11)));
  }

  @Test
  void testCompareTypeFail() {
    assertThrows(TensorRuntimeException.class, () -> Scalars.compare(GaussScalar.of(2, 7), RealScalar.of(0.3)));
    assertThrows(TensorRuntimeException.class, () -> Scalars.compare(RealScalar.of(0.3), GaussScalar.of(2, 7)));
  }

  @Test
  void testComparableFail() {
    assertThrows(TensorRuntimeException.class, () -> Scalars.compare(DoubleScalar.of(3.14), GaussScalar.of(1, 7)));
  }
}
