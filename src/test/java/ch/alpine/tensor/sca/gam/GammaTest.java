// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.NumberQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Chop;

public class GammaTest {
  @Test
  public void testFactorial() {
    for (int index = 0; index < 20; ++index)
      assertEquals(Gamma.of(RealScalar.of(index + 1)), Factorial.of(RealScalar.of(index)));
  }

  @Test
  public void testPositiveMathematica() {
    Scalar expect = ComplexScalar.of(0.25437472214867807, -1.9167734838293429);
    Scalar z = ComplexScalar.of(4.2, 3.4);
    Scalar result = Gamma.FUNCTION.apply(z);
    Tolerance.CHOP.requireClose(expect, result);
  }

  @Test
  public void testNegativeMathematica() {
    Scalar expect = ComplexScalar.of(-0.00004914938770081672, -0.00043368292844823665);
    Scalar z = ComplexScalar.of(-2.2, -3.3);
    Scalar result = Gamma.FUNCTION.apply(z);
    Tolerance.CHOP.requireClose(expect, result);
  }

  @Test
  public void testGammaNumPos() {
    Chop._08.requireClose(Gamma.of(RealScalar.of(3.0)), RealScalar.of(2));
    Chop._08.requireClose(Gamma.of(RealScalar.of(4.0)), RealScalar.of(6));
    Chop._08.requireClose(Gamma.of(RealScalar.of(5.0)), RealScalar.of(24));
    Tolerance.CHOP.requireClose(Gamma.of(RealScalar.of(3.2)), RealScalar.of(2.4239654799353683));
    Tolerance.CHOP.requireClose(Gamma.of(RealScalar.of(7.9)), RealScalar.of(4122.709484285446));
  }

  @Test
  public void testGammaNum() {
    Chop._08.requireClose(Gamma.of(RealScalar.of(2.0)), RealScalar.of(1));
    Chop._08.requireClose(Gamma.of(RealScalar.of(1.0)), RealScalar.of(1));
    Chop._08.requireClose(Gamma.of(RealScalar.of(-1.2)), RealScalar.of(4.850957140522099));
    Chop._08.requireClose(Gamma.of(RealScalar.of(-3.8)), RealScalar.of(0.29963213450284565));
    Tolerance.CHOP.requireClose(Gamma.of(RealScalar.of(-2.1)), RealScalar.of(-4.626098277572807));
  }

  @Test
  public void testLargeInt() {
    assertEquals(Gamma.of(RealScalar.of(-1000.2)), RealScalar.ZERO);
    assertEquals(Gamma.of(RealScalar.of(1000.2)), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  public void testLargeNegativeInteger() {
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(RealScalar.of(-100000000000L)));
  }

  @Test
  public void testLargeNegativeIntegerDouble() {
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(DecimalScalar.of(new BigDecimal("-100000000000.0"))));
  }

  @Test
  public void testLargeNegative() {
    assertEquals(Gamma.of(RealScalar.of(-23764528364522.345)), RealScalar.ZERO);
  }

  @Test
  public void testLargePoistive() {
    assertEquals(Gamma.of(RealScalar.of(1000000000000L)), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(Gamma.of(RealScalar.of(23764528364522.345)), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  public void testComplex1() {
    Scalar result = Gamma.of(ComplexScalar.of(1.1, 0.3));
    Scalar actual = ComplexScalar.of(0.886904759534451, -0.10608824042449128);
    Tolerance.CHOP.requireClose(result, actual);
  }

  @Test
  public void testComplex2() {
    Scalar result = Gamma.of(ComplexScalar.of(0, 1));
    Scalar actual = ComplexScalar.of(-0.15494982830181073, -0.4980156681183565);
    Tolerance.CHOP.requireClose(result, actual);
  }

  @Test
  public void testNest1() {
    Scalar seed = Scalars.fromString("-1.0894117647058823-0.07745098039215685*I");
    seed = Nest.of(Gamma.FUNCTION, seed, 3);
    // Mathematica gives
    // -4.371039232490273`*^-18 + 1.9336913999047586`*^-17 I
    // System.out.println(seed);
    // assertTrue(Chop._50.allZero(seed));
  }

  @Test
  public void testNest2() {
    Scalar seed = Scalars.fromString("-1.0486274509803923-0.028431372549019604*I");
    seed = Gamma.of(seed);
    seed = Gamma.of(seed);
    seed = Gamma.of(seed);
    assertFalse(NumberQ.of(seed));
  }

  @Test
  public void testMathematica() {
    Scalar z = ComplexScalar.of(3.2363893230567875, 2.665896822743508);
    Scalar result = Gamma.FUNCTION.apply(z);
    Scalar expect = ComplexScalar.of(-0.8000736272450161, 0.09101285585260227);
    Tolerance.CHOP.requireClose(result, expect);
  }

  @Test
  public void testInt0Fail() {
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(RealScalar.of(0)));
  }

  @Test
  public void testIntN1Fail() {
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(RealScalar.of(-1)));
  }

  @Test
  public void testDouble0Fail() {
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(RealScalar.of(0.0)));
  }

  @Test
  public void testDoubleN1Fail() {
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(RealScalar.of(-1.0)));
  }

  @Test
  public void testQuantityFail() {
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(Quantity.of(3, "m*s")));
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(Quantity.of(-2, "m"))); // <- fails for the wrong reason
    assertThrows(TensorRuntimeException.class, () -> Gamma.of(Quantity.of(-2.12, "m^2")));
  }
}
