// code by jph
package ch.alpine.tensor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class RealScalarTest extends TestCase {
  public void testSerializable() throws Exception {
    Scalar a = RealScalar.ZERO;
    Scalar b = Serialization.parse(Serialization.of(a));
    assertEquals(a, b);
    assertFalse(a == b);
  }

  public void testSign() {
    assertEquals(Sign.FUNCTION.apply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(Sign.FUNCTION.apply(RealScalar.of(+5)), RealScalar.ONE);
    assertEquals(Sign.FUNCTION.apply(RealScalar.of(-5)), RealScalar.ONE.negate());
    RealScalar r1 = (RealScalar) RationalScalar.of(1927365481254298736L, 1927365481254298737L);
    RealScalar r2 = (RealScalar) RationalScalar.of(1927365481254298741L, -1927365481254298739L);
    assertEquals(Sign.FUNCTION.apply(r1), RealScalar.ONE);
    assertEquals(Sign.FUNCTION.apply(r2), RealScalar.ONE.negate());
  }

  public void testCompare() {
    assertEquals(Scalars.compare(RealScalar.ZERO, RealScalar.ZERO), 0);
    {
      final Integer a = 0;
      final Integer b = 5;
      assertEquals(Integer.compare(0, b), Scalars.compare(RealScalar.ZERO, RealScalar.of(b)));
      assertEquals(a.compareTo(b), Scalars.compare(RealScalar.ZERO, RealScalar.of(b)));
    }
    {
      final Integer a = 0;
      final Integer b = -5;
      assertEquals(Integer.compare(0, b), Scalars.compare(RealScalar.ZERO, RealScalar.of(b)));
      assertEquals(a.compareTo(b), Scalars.compare(RealScalar.ZERO, RealScalar.of(b)));
    }
    {
      assertEquals(Double.compare(0.3, 0.4), Scalars.compare(DoubleScalar.of(0.3), DoubleScalar.of(0.4)));
      assertEquals(Double.compare(0.3, -4e10), Scalars.compare(DoubleScalar.of(0.3), DoubleScalar.of(-4e10)));
    }
  }

  public void testCompareDouble() {
    assertEquals(Double.compare(0.3, 0.4), Scalars.compare(DoubleScalar.of(0.3), DoubleScalar.of(0.4)));
    assertEquals(Double.compare(0.3, -4e10), Scalars.compare(DoubleScalar.of(0.3), DoubleScalar.of(-4e10)));
  }

  public void testCompareRational() {
    Scalar r1 = RationalScalar.of(1927365481254298736L, 1927365481254298737L);
    Scalar r2 = RationalScalar.of(1927365481254298741L, 1927365481254298739L);
    assertEquals(Scalars.compare(r1, r2), -1);
    Scalar d1 = DoubleScalar.of(r1.number().doubleValue());
    Scalar d2 = DoubleScalar.of(r2.number().doubleValue());
    assertEquals(Scalars.compare(d1, d2), 0);
  }

  public void testNumber() {
    assertEquals(RealScalar.ZERO, RealScalar.of(0));
    assertEquals(RealScalar.ZERO, RealScalar.of(0.));
    assertEquals(DoubleScalar.of(3.0), RealScalar.of(3.));
    assertEquals(DoubleScalar.of(3.0), RealScalar.of(3.f));
    assertEquals(RealScalar.of(3), RealScalar.of(3));
    assertEquals(RationalScalar.of(3, 1), RealScalar.of(3));
    assertEquals(RationalScalar.of(3, 1), RealScalar.of(3L));
    assertEquals(RationalScalar.of(1, 1), RealScalar.of(BigInteger.ONE));
    assertEquals(RationalScalar.of(1, 1), RealScalar.of(BigDecimal.ONE));
  }

  public void testNumberByte() {
    assertTrue(RealScalar.of(-1).number().byteValue() == (byte) 255);
    assertTrue(RealScalar.of(0).number().byteValue() == (byte) 0);
    assertTrue(RealScalar.of(1).number().byteValue() == (byte) 1);
    assertTrue(RealScalar.of(128).number().byteValue() == (byte) 128);
    assertTrue(RealScalar.of(255).number().byteValue() == (byte) 255);
    assertTrue(RealScalar.of(256).number().byteValue() == (byte) 0);
  }

  public void testNumberTypes() {
    assertEquals(RealScalar.of((byte) 0xff), RealScalar.ONE.negate());
    assertEquals(RealScalar.of((short) 0xffff), RealScalar.ONE.negate());
  }

  public void testInvertInfinity() {
    assertEquals(DoubleScalar.POSITIVE_INFINITY.reciprocal(), RealScalar.ZERO);
    assertEquals(DoubleScalar.NEGATIVE_INFINITY.reciprocal(), RealScalar.ZERO);
  }

  public void testBigInteger() {
    Scalar scalar = RealScalar.of(new BigInteger("123"));
    assertTrue(scalar instanceof RationalScalar);
    assertEquals(scalar, RealScalar.of(123));
  }

  public void testMiscPrimitives() {
    short vals = -312;
    assertEquals(RealScalar.of(vals), RealScalar.of(-312));
    ExactScalarQ.require(RealScalar.of(vals));
    byte valb = -122;
    assertEquals(RealScalar.of(valb), RealScalar.of(-122));
    ExactScalarQ.require(RealScalar.of(valb));
  }

  public void testMiscTypes() {
    Short vals = -312;
    assertEquals(RealScalar.of(vals), RealScalar.of(-312));
    ExactScalarQ.require(RealScalar.of(vals));
    Byte valb = -122;
    assertEquals(RealScalar.of(valb), RealScalar.of(-122));
    ExactScalarQ.require(RealScalar.of(valb));
    Number number = BigDecimal.TEN;
    assertEquals(RealScalar.of(number), RealScalar.of(10));
  }

  public void testCreateFail() {
    Number number = new AtomicInteger(123);
    Scalar scalar = RealScalar.of(number.intValue());
    assertEquals(scalar, RealScalar.of(123));
    AssertFail.of(() -> RealScalar.of(number));
  }

  public void testNullFail() {
    AssertFail.of(() -> RealScalar.of((Number) null));
  }
}
