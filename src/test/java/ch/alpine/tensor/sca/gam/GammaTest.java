// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Complex;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.chq.DeterminateScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Chop;

class GammaTest {
  @RepeatedTest(20)
  void testFactorial(RepetitionInfo repetitionInfo) {
    int index = repetitionInfo.getCurrentRepetition();
    assertEquals(Gamma.FUNCTION.apply(RealScalar.of(index)), Factorial.FUNCTION.apply(RealScalar.of(index - 1)));
  }

  @Test
  void testPositiveMathematica() {
    Scalar expect = Complex.of(0.25437472214867807, -1.9167734838293429);
    Scalar z = Complex.of(4.2, 3.4);
    Scalar result = Gamma.FUNCTION.apply(z);
    Tolerance.CHOP.requireClose(expect, result);
  }

  @Test
  void testNegativeMathematica() {
    Scalar expect = Complex.of(-0.00004914938770081672, -0.00043368292844823665);
    Scalar z = Complex.of(-2.2, -3.3);
    Scalar result = Gamma.FUNCTION.apply(z);
    Tolerance.CHOP.requireClose(expect, result);
  }

  @Test
  void testGammaNumPos() {
    Chop._08.requireClose(Gamma.FUNCTION.apply(RealScalar.of(3.0)), RealScalar.of(2));
    Chop._08.requireClose(Gamma.FUNCTION.apply(RealScalar.of(4.0)), RealScalar.of(6));
    Chop._08.requireClose(Gamma.FUNCTION.apply(RealScalar.of(5.0)), RealScalar.of(24));
    Tolerance.CHOP.requireClose(Gamma.FUNCTION.apply(RealScalar.of(3.2)), RealScalar.of(2.4239654799353683));
    Tolerance.CHOP.requireClose(Gamma.FUNCTION.apply(RealScalar.of(7.9)), RealScalar.of(4122.709484285446));
  }

  @Test
  void testGammaNum() {
    Chop._08.requireClose(Gamma.FUNCTION.apply(RealScalar.of(2.0)), RealScalar.of(1));
    Chop._08.requireClose(Gamma.FUNCTION.apply(RealScalar.of(1.0)), RealScalar.of(1));
    Chop._08.requireClose(Gamma.FUNCTION.apply(RealScalar.of(-1.2)), RealScalar.of(4.850957140522099));
    Chop._08.requireClose(Gamma.FUNCTION.apply(RealScalar.of(-3.8)), RealScalar.of(0.29963213450284565));
    Tolerance.CHOP.requireClose(Gamma.FUNCTION.apply(RealScalar.of(-2.1)), RealScalar.of(-4.626098277572807));
  }

  @Test
  void testLargeInt() {
    assertEquals(Gamma.FUNCTION.apply(RealScalar.of(-1000.2)), RealScalar.ZERO);
    assertEquals(Gamma.FUNCTION.apply(RealScalar.of(1000.2)), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testLargeNegativeInteger() {
    Scalar scalar = Gamma.FUNCTION.apply(RealScalar.of(-100000000000L));
    assertFalse(DeterminateScalarQ.of(scalar));
  }

  @Test
  void testLargeNegativeIntegerDouble() {
    Scalar scalar = Gamma.FUNCTION.apply(DecimalScalar.of(new BigDecimal("-100000000000.0")));
    assertFalse(DeterminateScalarQ.of(scalar));
  }

  @Test
  void testLargeNegative() {
    assertEquals(Gamma.FUNCTION.apply(RealScalar.of(-23764528364522.345)), RealScalar.ZERO);
  }

  @Test
  void testLargePoistive() {
    assertEquals(Gamma.FUNCTION.apply(RealScalar.of(1000000000000L)), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(Gamma.FUNCTION.apply(RealScalar.of(23764528364522.345)), DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testComplex1() {
    Scalar result = Gamma.FUNCTION.apply(Complex.of(1.1, 0.3));
    Scalar actual = Complex.of(0.886904759534451, -0.10608824042449128);
    Tolerance.CHOP.requireClose(result, actual);
  }

  @Test
  void testComplex2() {
    Scalar result = Gamma.FUNCTION.apply(Complex.of(0, 1));
    Scalar actual = Complex.of(-0.15494982830181073, -0.4980156681183565);
    Tolerance.CHOP.requireClose(result, actual);
  }

  @Test
  void testNest1() {
    Scalar seed = Complex.of(-1.0894117647058823, -0.07745098039215685);
    Scalar expect = Complex.of(-4.371039232490273e-18, 1.9336913999047586e-17); // Mathematica
    Scalar result = Nest.of(Gamma.FUNCTION, seed, 3);
    Chop._25.requireClose(expect, result);
  }

  @Test
  void testNest2() {
    Scalar seed = Complex.of(-1.0486274509803923, -0.028431372549019604);
    seed = Gamma.FUNCTION.apply(seed);
    seed = Gamma.FUNCTION.apply(seed);
    seed = Gamma.FUNCTION.apply(seed);
    assertFalse(FiniteScalarQ.of(seed));
  }

  @Test
  void testMathematica() {
    Scalar z = Complex.of(3.2363893230567875, 2.665896822743508);
    Scalar result = Gamma.FUNCTION.apply(z);
    Scalar expect = Complex.of(-0.8000736272450161, 0.09101285585260227);
    Tolerance.CHOP.requireClose(result, expect);
  }

  @ParameterizedTest
  @ValueSource(ints = { 0, -1, -2 })
  void testInt0Fail(int val) {
    assertFalse(DeterminateScalarQ.of(Gamma.FUNCTION.apply(RealScalar.of(val))));
    assertFalse(DeterminateScalarQ.of(Gamma.FUNCTION.apply(DoubleScalar.of(val))));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> Gamma.FUNCTION.apply(Quantity.of(3, "m*s")));
    assertThrows(Throw.class, () -> Gamma.FUNCTION.apply(Quantity.of(-2, "m"))); // <- fails for the wrong reason
    assertThrows(Throw.class, () -> Gamma.FUNCTION.apply(Quantity.of(-2.12, "m^2")));
  }
}
