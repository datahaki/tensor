// code by jph
package ch.alpine.tensor;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.io.StringScalarQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ScalarsTest extends TestCase {
  void checkInvariant(String string, Class<?> myclass) {
    Scalar s = Scalars.fromString(string);
    Scalar t = Scalars.fromString(s.toString());
    assertEquals(s, t);
    assertEquals(s.getClass(), myclass);
    assertEquals(t.getClass(), myclass);
  }

  public void testParse() {
    checkInvariant("123", RationalScalar.class);
    checkInvariant("  123  ", RationalScalar.class);
    checkInvariant("3 /  4", RationalScalar.class);
    checkInvariant("34.23123", DoubleScalar.class);
    checkInvariant("0", RationalScalar.class);
    checkInvariant("12+15 /4*I", ComplexScalarImpl.class);
    checkInvariant("1.0E-50 + 1.0E50*I", ComplexScalarImpl.class);
    checkInvariant("I", ComplexScalarImpl.class);
    checkInvariant(" ( I ) ", ComplexScalarImpl.class);
    checkInvariant("123123*I", ComplexScalarImpl.class);
    checkInvariant("123E-123*I", ComplexScalarImpl.class);
    checkInvariant("asndbvf", StringScalar.class);
    checkInvariant("asn.dbv.f", StringScalar.class);
    checkInvariant("123-1A23*I", StringScalar.class);
  }

  public void testParseSpecific() {
    assertEquals(Scalars.fromString("+002.5"), DoubleScalar.of(+2.5));
    assertEquals(Scalars.fromString("-002.5"), DoubleScalar.of(-2.5));
  }

  public void testSpacing() {
    checkInvariant("-1.0348772853950305  +  0.042973906265653894 * I", ComplexScalarImpl.class);
    checkInvariant("-1.0348772853950305  -  0.042973906265653894 * I", ComplexScalarImpl.class);
  }

  public void testIntegerPattern() {
    String n1 = "-123123";
    String n2 = "123123";
    Pattern pattern = Pattern.compile("-?\\d+");
    assertTrue(pattern.matcher(n1).matches());
    assertTrue(pattern.matcher(n2).matches());
    Predicate<String> predicate = pattern.asMatchPredicate();
    assertTrue(predicate.test(n1));
    assertTrue(predicate.test(n2));
  }

  public void testRationalPattern() {
    String n1 = "-123/123";
    String n2 = "1231/23";
    String n3 = "123123";
    Pattern pattern = Pattern.compile("-?\\d+/\\d+");
    assertTrue(pattern.matcher(n1).matches());
    assertTrue(pattern.matcher(n2).matches());
    assertFalse(pattern.matcher(n3).matches());
    Predicate<String> predicate = pattern.asMatchPredicate();
    assertTrue(predicate.test(n1));
    assertTrue(predicate.test(n2));
    assertFalse(predicate.test(n3));
  }

  public void testParseComplex() {
    checkInvariant(ComplexScalar.of(-1e-14, -1e-15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(+1e-14, -1e-15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(-1e+14, -1e-15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(+1e+14, -1e-15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(-1e-14, -1e+15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(+1e-14, -1e+15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(-1e+14, -1e+15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(+1e+14, -1e+15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(-1e-14, +1e-15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(+1e-14, +1e-15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(-1e+14, +1e-15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(+1e+14, +1e-15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(-1e-14, +1e+15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(+1e-14, +1e+15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(-1e+14, +1e+15).toString(), ComplexScalarImpl.class);
    checkInvariant(ComplexScalar.of(+1e+14, +1e+15).toString(), ComplexScalarImpl.class);
  }

  public void testImagUnit() {
    assertEquals("I", ComplexScalar.I.toString());
    assertEquals("-I", ComplexScalar.I.negate().toString());
    assertEquals("2+I", RealScalar.of(2).add(ComplexScalar.I).toString());
    assertEquals("2-I", RealScalar.of(2).subtract(ComplexScalar.I).toString());
    // ---
    assertEquals("3*I", ComplexScalar.of(0, 3).toString());
    assertEquals("3-3*I", ComplexScalar.of(3, -3).toString());
    assertEquals("3+3*I", ComplexScalar.of(3, 3).toString());
    assertEquals("-3*I", ComplexScalar.of(0, -3).toString());
    assertEquals("-3-3*I", ComplexScalar.of(-3, -3).toString());
    assertEquals("-3+3*I", ComplexScalar.of(-3, 3).toString());
  }

  public void testNumber() {
    Number a = 123;
    Number b = 123.0;
    assertFalse(a.equals(b));
  }

  private static void checkCmp(double d1, double d2) {
    int result = Scalars.compare(RealScalar.of(d1), RealScalar.of(d2));
    assertEquals(Double.compare(d1, d2), result);
    int swpped = Scalars.compare(RealScalar.of(d2), RealScalar.of(d1));
    assertEquals(swpped, -result);
  }

  public void testStatic() {
    assertTrue(Scalars.compare(RealScalar.of(2), RealScalar.of(3)) < 0);
    assertTrue(Scalars.compare(RealScalar.of(5), RealScalar.of(1)) > 0);
    assertTrue(Scalars.compare(RealScalar.of(8), RealScalar.of(8)) == 0);
  }

  public void testExtreme() {
    checkInvariant(DoubleScalar.NEGATIVE_INFINITY.toString(), DoubleScalar.class);
    checkInvariant(DoubleScalar.POSITIVE_INFINITY.toString(), DoubleScalar.class);
  }

  public void testCompare() {
    checkCmp(0, 0);
    checkCmp(1, 0);
    checkCmp(1.1, 1.1);
    checkCmp(1, 5);
    checkCmp(-1e10, 5);
    checkCmp(Double.POSITIVE_INFINITY, 5);
    checkCmp(Double.NEGATIVE_INFINITY, 5);
    checkCmp(0, Double.POSITIVE_INFINITY);
    checkCmp(0, Double.NEGATIVE_INFINITY);
    checkCmp(-10, Double.POSITIVE_INFINITY);
    checkCmp(-30, Double.NEGATIVE_INFINITY);
  }

  public void testLessThan() {
    assertFalse(Scalars.lessThan(RealScalar.of(2), RealScalar.of(2)));
    assertTrue(Scalars.lessThan(RealScalar.of(2), RealScalar.of(3)));
    assertTrue(Scalars.lessThan(RealScalar.of(-3), RealScalar.ZERO));
  }

  public void testLessEquals() {
    assertTrue(Scalars.lessEquals(RealScalar.of(2), RealScalar.of(2)));
    assertTrue(Scalars.lessEquals(RealScalar.of(2), RealScalar.of(3)));
    assertTrue(Scalars.lessEquals(RealScalar.of(-3), RealScalar.ZERO));
  }

  public void testIntValueExact() {
    assertEquals(Scalars.intValueExact(RealScalar.of(123)), 123);
    assertEquals(Scalars.intValueExact(RealScalar.of(Integer.MIN_VALUE)), Integer.MIN_VALUE);
    assertEquals(Scalars.intValueExact(RealScalar.of(Integer.MAX_VALUE)), Integer.MAX_VALUE);
  }

  public void testIntValueExactFail() {
    AssertFail.of(() -> Scalars.intValueExact(RealScalar.of(Long.MIN_VALUE)));
    AssertFail.of(() -> Scalars.intValueExact(RealScalar.of(Long.MAX_VALUE)));
  }

  public void testIntValueExactFractionFail() {
    AssertFail.of(() -> Scalars.intValueExact(RationalScalar.of(2, 3)));
  }

  public void testLongValueExact() {
    assertEquals(Scalars.longValueExact(RealScalar.of(123)), 123);
    assertEquals(Scalars.longValueExact(RealScalar.of(123)), 123L);
    assertEquals(Scalars.longValueExact(RealScalar.of(Long.MIN_VALUE)), Long.MIN_VALUE);
    assertEquals(Scalars.longValueExact(RealScalar.of(Long.MAX_VALUE)), Long.MAX_VALUE);
  }

  public void testExample() {
    Scalar s = Scalars.fromString("(3+2)*I/(-1+4)+8-I");
    Scalar c = ComplexScalar.of(RealScalar.of(8), RationalScalar.of(2, 3));
    assertEquals(c, s);
    assertEquals(s, c);
  }

  public void testParseFail() {
    assertTrue(StringScalarQ.of(Scalars.fromString("(3+2)(-1+4")));
    assertTrue(Scalars.fromString("(3+2)(-1+4+") instanceof StringScalar);
    assertTrue(Scalars.fromString("3+2-1+4+") instanceof StringScalar);
    assertTrue(Scalars.fromString("3+2-1+4-") instanceof StringScalar);
    assertTrue(Scalars.fromString("3++4") instanceof StringScalar);
    assertTrue(Scalars.fromString("3--4") instanceof StringScalar);
    assertTrue(Scalars.fromString("3**4") instanceof StringScalar);
    assertTrue(StringScalarQ.of(Scalars.fromString("3//4")));
  }

  public void testDivides() {
    assertTrue(Scalars.divides(RealScalar.of(3), RealScalar.of(9)));
    assertFalse(Scalars.divides(RealScalar.of(9), RealScalar.of(3)));
    assertFalse(Scalars.divides(RealScalar.of(2), RealScalar.of(9)));
    assertTrue(Scalars.divides(RationalScalar.of(3, 7), RationalScalar.of(18, 7)));
    assertFalse(Scalars.divides(RationalScalar.of(3, 7), RationalScalar.of(8, 7)));
  }

  public void testComplex() {
    Scalar c2 = ComplexScalar.of(2, 3);
    Scalar c1 = c2.multiply(RealScalar.of(3));
    assertFalse(Scalars.divides(c1, c2));
    assertTrue(Scalars.divides(c2, c1));
  }

  public void testGaussian() {
    Scalar c1 = ComplexScalar.of(3, 1);
    Scalar c2 = ComplexScalar.of(2, -1);
    assertFalse(Scalars.divides(c1, c2));
    assertTrue(Scalars.divides(c2, c1));
  }

  public void testQuantity() {
    assertTrue(Scalars.divides(Quantity.of(3, "m"), Quantity.of(9, "m")));
    assertFalse(Scalars.divides(Quantity.of(3, "m"), Quantity.of(7, "m")));
    assertFalse(Scalars.divides(Quantity.of(7, "m"), Quantity.of(3, "m")));
  }

  public void testQuantityIncompatible() {
    Scalar qs1 = Quantity.of(6, "m");
    Scalar qs2 = Quantity.of(3, "s");
    AssertFail.of(() -> Scalars.divides(qs1, qs2));
  }

  public void testBigIntegerExactNullFail() {
    AssertFail.of(() -> Scalars.bigIntegerValueExact(null));
  }

  public void testOptionalBigIntegerNullFail() {
    AssertFail.of(() -> Scalars.optionalBigInteger(null));
  }
}
