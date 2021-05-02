// code by jph
package ch.alpine.tensor.sca;

import java.math.BigDecimal;
import java.math.MathContext;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PowerTest extends TestCase {
  public void testInteger() {
    assertEquals(Power.of(2, 4), RealScalar.of(16));
    assertEquals(Power.of(-4, 5), RealScalar.of(-1024));
  }

  public void testSqrtExactHalfPos() {
    Scalar scalar = Power.function(RationalScalar.HALF).apply(RealScalar.of(4));
    assertEquals(ExactScalarQ.require(scalar), RealScalar.of(2));
  }

  public void testSqrtExactHalfNeg() {
    Scalar scalar = Power.function(RationalScalar.HALF.negate()).apply(RealScalar.of(9));
    assertEquals(ExactScalarQ.require(scalar), RationalScalar.of(1, 3));
  }

  public void testExponentZero() {
    assertEquals(Power.of(+2, 0), RealScalar.ONE);
    assertEquals(Power.of(+1, 0), RealScalar.ONE);
    assertEquals(Power.of(+0, 0), RealScalar.ONE);
    assertEquals(Power.of(-1, 0), RealScalar.ONE);
    assertEquals(Power.of(-2, 0), RealScalar.ONE);
  }

  public void testNumberScalar() {
    Scalar scalar = Power.of(2, RationalScalar.of(2, 3));
    Chop._13.requireClose(scalar, Scalars.fromString("1.5874010519681994`"));
  }

  public void testSqrt() {
    assertEquals(Power.of(2, 0.5), Sqrt.of(RealScalar.of(2)));
    assertEquals(Power.of(14, 0.5), Sqrt.of(RealScalar.of(14)));
  }

  public void testZero() {
    assertEquals(Power.of(0, +0), RealScalar.ONE);
    assertEquals(Power.of(0, +1), RealScalar.ZERO);
    assertEquals(Power.of(0, +2), RealScalar.ZERO);
  }

  public void testZeroFail() {
    AssertFail.of(() -> Power.of(0, -2));
  }

  public void testNegativeOne() {
    assertEquals(Power.of(-1, 0), RealScalar.ONE);
    assertEquals(Power.of(-1, 1), RealScalar.ONE.negate());
    assertEquals(Power.of(-1, 2), RealScalar.ONE);
    assertEquals(Power.of(-1, 3), RealScalar.ONE.negate());
  }

  public void testZeroComplex() {
    assertEquals(Power.of(RealScalar.ZERO, Scalars.fromString("0.1+3*I")), RealScalar.ZERO);
    assertEquals(Power.of(RealScalar.ZERO, Scalars.fromString("0.1-3*I/2")), RealScalar.ZERO);
  }

  public void testZeroComplex1Fail() {
    AssertFail.of(() -> Power.of(RealScalar.ZERO, ComplexScalar.I));
  }

  public void testZeroComplex2Fail() {
    AssertFail.of(() -> Power.of(RealScalar.ZERO, Scalars.fromString("-0.1+3*I")));
  }

  public void testNegative() {
    assertEquals(Power.of(2, -4), RealScalar.of(16).reciprocal());
    assertEquals(Power.of(-4, -5), RealScalar.of(-1024).reciprocal());
  }

  public void testNegativeFractional() {
    Scalar result = Power.of(-2.2, 1.3);
    Scalar gndtru = Scalars.fromString("-1.6382047104755275 - 2.254795345529229* I");
    assertEquals(result, gndtru);
  }

  public void testNegativeFractionalNeg() {
    Scalar result = Power.of(-2.2, -1.3);
    Scalar gndtru = Scalars.fromString("-0.21089641642663778` + 0.290274014661784` *I ");
    assertEquals(result, gndtru);
  }

  public void testComplex() {
    Scalar a = ComplexScalar.of(2, +3);
    Scalar b = ComplexScalar.of(4, -2);
    Scalar c = Power.of(a, b);
    // Mathematica: 245.099 + 1181.35 I
    assertEquals(c, Scalars.fromString("245.09854196562927+1181.3509801973048*I"));
  }

  public void testFunction() {
    assertEquals(RealScalar.of(7).map(Power.function(0.5)), Sqrt.of(RealScalar.of(7)));
    assertEquals(Power.function(0.5).apply(RealScalar.of(7)), Sqrt.of(RealScalar.of(7)));
  }

  public void testTypeFail() {
    Scalar scalar = StringScalar.of("some");
    AssertFail.of(() -> Power.of(scalar, 0));
  }

  public void testDecimal() {
    MathContext mc = MathContext.DECIMAL128;
    Scalar d1 = DecimalScalar.of(new BigDecimal("1.234", mc), mc.getPrecision());
    assertEquals(Power.of(d1, 2.34), DoubleScalar.of(Math.pow(1.234, 2.34)));
  }

  public void testGaussScalar() {
    Scalar scalar = GaussScalar.of(6, 31);
    AssertFail.of(() -> Power.of(scalar, 3.13));
  }

  public void testQuantity1() {
    Scalar qs1 = Quantity.of(9, "m^2");
    Scalar res = Power.of(qs1, RealScalar.of(3));
    Scalar act = Quantity.of(729, "m^6");
    assertEquals(res, act);
    Scalar sqr = Power.of(qs1, RationalScalar.HALF);
    ExactScalarQ.require(sqr);
    assertEquals(sqr, Quantity.of(3, "m"));
  }

  public void testQuantity2() {
    Scalar qs1 = Quantity.of(-2, "m^-3*rad");
    Scalar res = Power.of(qs1, RealScalar.of(3));
    Scalar act = Quantity.of(-8, "m^-9*rad^3");
    assertEquals(res, act);
  }

  public void testQuantityFail() {
    Scalar qs1 = Quantity.of(2, "cd");
    Scalar qs2 = Quantity.of(4, "cd");
    AssertFail.of(() -> Power.of(qs1, qs2));
  }

  public void testFailNullNumber() {
    AssertFail.of(() -> Power.function((Number) null));
  }

  public void testFailNullScalar() {
    AssertFail.of(() -> Power.function((Scalar) null));
  }
}
