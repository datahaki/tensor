// code by jph
package ch.ethz.idsc.tensor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import junit.framework.TestCase;

public class BigDecimalTest extends TestCase {
  public void testPrecision() {
    BigDecimal value = new BigDecimal(new BigInteger("12333"), new MathContext(100, RoundingMode.HALF_EVEN));
    value = new BigDecimal("12333", new MathContext(100, RoundingMode.HALF_EVEN));
    value.precision();
    int precision = value.precision();
    assertTrue(0 < precision);
  }

  public void testMultiply() {
    BigDecimal a = new BigDecimal("19.2134534512334534343");
    assertEquals(a.precision(), 21);
    BigDecimal b = new BigDecimal("33.8375654545222327883");
    BigDecimal ab1 = a.multiply(b);
    assertEquals(ab1.precision(), 41);
    BigDecimal ab2 = a.multiply(b, MathContext.DECIMAL128);
    assertEquals(ab2.precision(), 34);
    BigDecimal c = new BigDecimal("192134534512.334534343");
    assertEquals(c.precision(), 21);
    BigDecimal d = new BigDecimal("192134534512334534343");
    assertEquals(d.precision(), 21);
  }

  public void testDivide() {
    BigDecimal a = new BigDecimal("0.0002134534512334534343");
    BigDecimal b = new BigDecimal("33333.8375654545222327883");
    BigDecimal ab2 = a.divide(b, MathContext.DECIMAL128);
    assertEquals(ab2.precision(), 34);
  }

  public void testSqrt() {
    MathContext mc = MathContext.DECIMAL128;
    BigDecimal b = new BigDecimal("29.1373503383756545452223278558123399996876");
    assertEquals(b.precision(), 42);
    BigDecimal sqrt = BigDecimalMath.sqrt(b, mc);
    assertEquals(sqrt.precision(), 34);
    BigDecimal r1 = sqrt.multiply(sqrt, mc);
    BigDecimal r2 = b.round(mc);
    assertEquals(r1, r2);
  }
}
