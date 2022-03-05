// code by jph
package ch.alpine.tensor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Cosh;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinh;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class BigDecimalMathTest extends TestCase {
  private static void _check(BigDecimal bd1, MathContext mathContext) {
    BigDecimal rt1 = BigDecimalMath.sqrt(bd1, MathContext.DECIMAL32);
    assertEquals(rt1.multiply(rt1, mathContext).subtract(bd1).compareTo(BigDecimal.ZERO), 0);
  }

  public void testSqrtSimple() {
    _check(new BigDecimal("4767655423.1", new MathContext(2, RoundingMode.HALF_EVEN)), MathContext.DECIMAL32);
    _check(new BigDecimal("5423.1", new MathContext(2, RoundingMode.HALF_EVEN)), MathContext.DECIMAL32);
    _check(new BigDecimal("0.000001235423", new MathContext(2, RoundingMode.HALF_EVEN)), MathContext.DECIMAL32);
    _check(new BigDecimal("0.000000000000000000000000000000000001"), MathContext.DECIMAL128);
    _check(new BigDecimal("0"), MathContext.DECIMAL128);
    _check(BigDecimal.ZERO, MathContext.DECIMAL128);
    _check(new BigDecimal("1"), MathContext.DECIMAL128);
    _check(BigDecimal.ONE, MathContext.DECIMAL128);
  }

  public void testSqrtExact() {
    BigDecimal bd1 = new BigDecimal(BigInteger.valueOf(25 * 25));
    Object object = BigDecimalMath.sqrt(bd1, MathContext.DECIMAL32);
    assertFalse(object.equals(RationalScalar.of(25, 1))); // gives false
  }

  public void testSqrtComparable() {
    BigDecimal bd1 = new BigDecimal(BigInteger.valueOf(25 * 25));
    Comparable<BigDecimal> rt1 = BigDecimalMath.sqrt(bd1, MathContext.DECIMAL32);
    assertEquals(rt1.compareTo(new BigDecimal("25")), 0);
  }

  public void testSqrtTwo() {
    BigDecimal bd1 = new BigDecimal("2");
    BigDecimal rt1 = BigDecimalMath.sqrt(bd1, new MathContext(100, RoundingMode.HALF_EVEN));
    // mathematica N[Sqrt[2], 100] gives
    String m = "1.414213562373095048801688724209698078569671875376948073176679737990732478462107038850387534327641573";
    assertEquals(rt1.toString(), m);
  }

  public void testSqrtSome() {
    BigDecimal bigDecimal = new BigDecimal("0.5138888888888888888888888888888885");
    assertEquals(bigDecimal.precision(), 34);
    // BigDecimalMath.sqrt(bigDecimal,
    Scalar scalar = DecimalScalar.of(bigDecimal);
    Sqrt.FUNCTION.apply(scalar);
    // BigDecimalMath.sqrt(bigDecimal, new MathContext(34));
  }

  public void testSqrtZero() {
    BigDecimal bd1 = new BigDecimal("0");
    BigDecimal rt1 = BigDecimalMath.sqrt(bd1, MathContext.DECIMAL64);
    assertEquals(rt1.compareTo(BigDecimal.ZERO), 0);
  }

  public void testSqrtNegative() {
    AssertFail.of(() -> BigDecimalMath.sqrt(new BigDecimal("-2340"), MathContext.DECIMAL64));
  }

  public void testExpZero() {
    BigDecimal bd1 = BigDecimalMath.exp(BigDecimal.ZERO, MathContext.DECIMAL128);
    assertEquals(bd1, BigDecimal.ONE);
  }

  public void testExp() {
    BigDecimal bd1 = BigDecimalMath.exp(BigDecimal.ONE, MathContext.DECIMAL128);
    assertEquals(bd1.toString(), "2.718281828459045235360287471352662"); // mathematica N[Exp[1], 34]
  }

  public void testSin() {
    // mathematica: 0.90929742682568169539601986591174484270225497144789
    // ............ 0.9092974268256816953960198659117451
    Scalar s0 = Sin.of(RealScalar.of(2));
    BigDecimal bd1 = BigDecimalMath.sin(BigDecimal.valueOf(2), MathContext.DECIMAL128);
    Chop._13.requireClose(s0, RealScalar.of(bd1.doubleValue()));
  }

  public void testSinh() {
    // mathematica: 0.90929742682568169539601986591174484270225497144789
    // ............ 0.9092974268256816953960198659117451
    Scalar s0 = Sinh.of(RealScalar.of(2));
    BigDecimal bd1 = BigDecimalMath.sinh(BigDecimal.valueOf(2), MathContext.DECIMAL128);
    Chop._13.requireClose(s0, RealScalar.of(bd1.doubleValue()));
  }

  public void testCos() {
    Scalar s0 = Cos.of(RealScalar.of(2));
    BigDecimal bd1 = BigDecimalMath.cos(BigDecimal.valueOf(2), MathContext.DECIMAL128);
    Chop._13.requireClose(s0, RealScalar.of(bd1.doubleValue()));
  }

  public void testCosh() {
    Scalar s0 = Cosh.of(RealScalar.of(2));
    BigDecimal bd1 = BigDecimalMath.cosh(BigDecimal.valueOf(2), MathContext.DECIMAL128);
    Chop._13.requireClose(s0, RealScalar.of(bd1.doubleValue()));
  }
}
