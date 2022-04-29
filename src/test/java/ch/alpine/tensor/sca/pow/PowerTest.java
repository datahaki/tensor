// code by jph
package ch.alpine.tensor.sca.pow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

public class PowerTest {
  @Test
  public void testInteger() {
    assertEquals(Power.of(2, 4), RealScalar.of(16));
    assertEquals(Power.of(-4, 5), RealScalar.of(-1024));
  }

  @Test
  public void testSqrtExactHalfPos() {
    Scalar scalar = Power.function(RationalScalar.HALF).apply(RealScalar.of(4));
    assertEquals(ExactScalarQ.require(scalar), RealScalar.of(2));
  }

  @Test
  public void testSqrtExactHalfNeg() {
    Scalar scalar = Power.function(RationalScalar.HALF.negate()).apply(RealScalar.of(9));
    assertEquals(ExactScalarQ.require(scalar), RationalScalar.of(1, 3));
  }

  @Test
  public void testExponentZero() {
    assertEquals(Power.of(+2, 0), RealScalar.ONE);
    assertEquals(Power.of(+1, 0), RealScalar.ONE);
    assertEquals(Power.of(+0, 0), RealScalar.ONE);
    assertEquals(Power.of(-1, 0), RealScalar.ONE);
    assertEquals(Power.of(-2, 0), RealScalar.ONE);
  }

  @Test
  public void testNumberScalar() {
    Scalar scalar = Power.of(2, RationalScalar.of(2, 3));
    Chop._13.requireClose(scalar, Scalars.fromString("1.5874010519681994`"));
  }

  @Test
  public void testSqrt() {
    assertEquals(Power.of(2, 0.5), Sqrt.of(RealScalar.of(2)));
    assertEquals(Power.of(14, 0.5), Sqrt.of(RealScalar.of(14)));
  }

  @Test
  public void testZero() {
    assertEquals(Power.of(0, +0), RealScalar.ONE);
    assertEquals(Power.of(0, +1), RealScalar.ZERO);
    assertEquals(Power.of(0, +2), RealScalar.ZERO);
  }

  @Test
  public void testZeroFail() {
    assertThrows(ArithmeticException.class, () -> Power.of(0, -2));
  }

  @Test
  public void testNegativeOne() {
    assertEquals(Power.of(-1, 0), RealScalar.ONE);
    assertEquals(Power.of(-1, 1), RealScalar.ONE.negate());
    assertEquals(Power.of(-1, 2), RealScalar.ONE);
    assertEquals(Power.of(-1, 3), RealScalar.ONE.negate());
  }

  @Test
  public void testZeroComplex() {
    assertEquals(Power.of(RealScalar.ZERO, Scalars.fromString("0.1+3*I")), RealScalar.ZERO);
    assertEquals(Power.of(RealScalar.ZERO, Scalars.fromString("0.1-3*I/2")), RealScalar.ZERO);
  }

  @Test
  public void testZeroComplex1Fail() {
    assertThrows(TensorRuntimeException.class, () -> Power.of(RealScalar.ZERO, ComplexScalar.I));
  }

  @Test
  public void testZeroComplex2Fail() {
    assertThrows(TensorRuntimeException.class, () -> Power.of(RealScalar.ZERO, Scalars.fromString("-0.1+3*I")));
  }

  @Test
  public void testNegative() {
    assertEquals(Power.of(2, -4), RealScalar.of(16).reciprocal());
    assertEquals(Power.of(-4, -5), RealScalar.of(-1024).reciprocal());
  }

  @Test
  public void testNegativeFractional() {
    Scalar result = Power.of(-2.2, 1.3);
    Scalar gndtru = Scalars.fromString("-1.6382047104755275 - 2.254795345529229* I");
    assertEquals(result, gndtru);
  }

  @Test
  public void testNegativeFractionalNeg() {
    Scalar result = Power.of(-2.2, -1.3);
    Scalar gndtru = Scalars.fromString("-0.21089641642663778` + 0.290274014661784` *I ");
    assertEquals(result, gndtru);
  }

  @Test
  public void testComplex() {
    Scalar a = ComplexScalar.of(2, +3);
    Scalar b = ComplexScalar.of(4, -2);
    Scalar c = Power.of(a, b);
    // Mathematica: 245.099 + 1181.35 I
    assertEquals(c, Scalars.fromString("245.09854196562927+1181.3509801973048*I"));
  }

  @Test
  public void testFunction() {
    assertEquals(RealScalar.of(7).map(Power.function(0.5)), Sqrt.of(RealScalar.of(7)));
    assertEquals(Power.function(0.5).apply(RealScalar.of(7)), Sqrt.of(RealScalar.of(7)));
  }

  @Test
  public void testTypeFail() {
    Scalar scalar = StringScalar.of("some");
    assertThrows(TensorRuntimeException.class, () -> Power.of(scalar, 0));
  }

  @Test
  public void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    Scalar d1 = DecimalScalar.of(new BigDecimal("1.234", mc), mc.getPrecision());
    assertEquals(Power.of(d1, 2.34), DoubleScalar.of(Math.pow(1.234, 2.34)));
  }

  @Test
  public void testGaussScalar() {
    Scalar scalar = GaussScalar.of(6, 31);
    assertThrows(TensorRuntimeException.class, () -> Power.of(scalar, 3.13));
  }

  @Test
  public void testQuantity1() {
    Scalar qs1 = Quantity.of(9, "m^2");
    Scalar res = Power.of(qs1, RealScalar.of(3));
    Scalar act = Quantity.of(729, "m^6");
    assertEquals(res, act);
    Scalar sqr = Power.of(qs1, RationalScalar.HALF);
    ExactScalarQ.require(sqr);
    assertEquals(sqr, Quantity.of(3, "m"));
  }

  @Test
  public void testQuantity2() {
    Scalar qs1 = Quantity.of(-2, "m^-3*rad");
    Scalar res = Power.of(qs1, RealScalar.of(3));
    Scalar act = Quantity.of(-8, "m^-9*rad^3");
    assertEquals(res, act);
  }

  @Test
  public void testQuantityFail() {
    Scalar qs1 = Quantity.of(2, "cd");
    Scalar qs2 = Quantity.of(4, "cd");
    assertThrows(TensorRuntimeException.class, () -> Power.of(qs1, qs2));
  }

  @Test
  public void testNaN() {
    assertEquals(Power.of(Double.NaN, Double.NaN).toString(), "NaN");
    assertEquals(Power.of(1, Double.NaN).toString(), "NaN");
    assertEquals(Power.of(Double.NaN, 1).toString(), "NaN");
    assertEquals(Power.of(1.2, Double.NaN).toString(), "NaN");
    assertEquals(Power.of(Double.NaN, 1.3).toString(), "NaN");
  }

  @Test
  public void testFailNullNumber() {
    assertThrows(NullPointerException.class, () -> Power.function((Number) null));
  }

  @Test
  public void testFailNullScalar() {
    assertThrows(NullPointerException.class, () -> Power.function((Scalar) null));
  }
}
