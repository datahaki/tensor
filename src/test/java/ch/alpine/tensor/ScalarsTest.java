// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;

class ScalarsTest {
  @Test
  void testRequireZero() {
    assertEquals(Scalars.requireZero(Quantity.of(0, "A")), Quantity.of(0, "A"));
    assertThrows(Throw.class, () -> Scalars.requireZero(Quantity.of(1, "A")));
    assertThrows(Throw.class, () -> Scalars.requireZero(RealScalar.ONE));
  }

  void checkInvariant(String string, Class<?> cls) {
    Scalar s = Scalars.fromString(string);
    Scalar t = Scalars.fromString(s.toString());
    assertEquals(s, t);
    assertEquals(s.getClass(), cls);
    assertEquals(t.getClass(), cls);
  }

  @ParameterizedTest
  @ValueSource(strings = { "123", "  123  ", "3 /  4", "0" })
  void testParseRationalScalar(String string) {
    checkInvariant(string, RationalScalar.class);
  }

  @ParameterizedTest
  @ValueSource(strings = { //
      "12+15 /4*I", //
      "1.0E-50 + 1.0E50*I", //
      "I", //
      " ( I ) ", //
      "123123*I", //
      "123E-123*I" })
  void testParseComplexScalar(String string) {
    checkInvariant(string, ComplexScalarImpl.class);
  }

  @Test
  void testParse() {
    checkInvariant("34.23123", DoubleScalar.class);
    checkInvariant("asndbvf", StringScalar.class);
    checkInvariant("asn.dbv.f", StringScalar.class);
    checkInvariant("123-1A23*I", StringScalar.class);
  }

  @Test
  void testParseSpecific() {
    assertEquals(Scalars.fromString("+002.5"), DoubleScalar.of(+2.5));
    assertEquals(Scalars.fromString("-002.5"), DoubleScalar.of(-2.5));
  }

  @Test
  void testSpacing() {
    checkInvariant("-1.0348772853950305  +  0.042973906265653894 * I", ComplexScalarImpl.class);
    checkInvariant("-1.0348772853950305  -  0.042973906265653894 * I", ComplexScalarImpl.class);
  }

  @Test
  void testIntegerPattern() {
    String n1 = "-123123";
    String n2 = "123123";
    Pattern pattern = Pattern.compile("-?\\d+");
    assertTrue(pattern.matcher(n1).matches());
    assertTrue(pattern.matcher(n2).matches());
    Predicate<String> predicate = pattern.asMatchPredicate();
    assertTrue(predicate.test(n1));
    assertTrue(predicate.test(n2));
  }

  @Test
  void testRationalPattern() {
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

  @Test
  void testParseComplex() {
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

  @Test
  void testImagUnit() {
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

  @Test
  void testNumber() {
    Number a = 123;
    Number b = 123.0;
    assertNotEquals(a, b);
  }

  private static void checkCmp(double d1, double d2) {
    int result = Scalars.compare(RealScalar.of(d1), RealScalar.of(d2));
    assertEquals(Double.compare(d1, d2), result);
    int swpped = Scalars.compare(RealScalar.of(d2), RealScalar.of(d1));
    assertEquals(swpped, -result);
  }

  @Test
  void testStatic() {
    assertTrue(Scalars.compare(RealScalar.of(2), RealScalar.of(3)) < 0);
    assertTrue(Scalars.compare(RealScalar.of(5), RealScalar.of(1)) > 0);
    assertEquals(Scalars.compare(RealScalar.of(8), RealScalar.of(8)), 0);
  }

  @Test
  void testExtreme() {
    checkInvariant(DoubleScalar.NEGATIVE_INFINITY.toString(), DoubleScalar.class);
    checkInvariant(DoubleScalar.POSITIVE_INFINITY.toString(), DoubleScalar.class);
  }

  @Test
  void testCompare() {
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

  @Test
  void testLessThan() {
    assertFalse(Scalars.lessThan(RealScalar.of(2), RealScalar.of(2)));
    assertTrue(Scalars.lessThan(RealScalar.of(2), RealScalar.of(3)));
    assertTrue(Scalars.lessThan(RealScalar.of(-3), RealScalar.ZERO));
  }

  @Test
  void testLessEquals() {
    assertTrue(Scalars.lessEquals(RealScalar.of(2), RealScalar.of(2)));
    assertTrue(Scalars.lessEquals(RealScalar.of(2), RealScalar.of(3)));
    assertTrue(Scalars.lessEquals(RealScalar.of(-3), RealScalar.ZERO));
  }

  @Test
  void testIntValueExact() {
    assertEquals(Scalars.intValueExact(RealScalar.of(123)), 123);
    assertEquals(Scalars.intValueExact(RealScalar.of(Integer.MIN_VALUE)), Integer.MIN_VALUE);
    assertEquals(Scalars.intValueExact(RealScalar.of(Integer.MAX_VALUE)), Integer.MAX_VALUE);
  }

  @Test
  void testIntValueExactFail() {
    assertThrows(ArithmeticException.class, () -> Scalars.intValueExact(RealScalar.of(Long.MIN_VALUE)));
    assertThrows(ArithmeticException.class, () -> Scalars.intValueExact(RealScalar.of(Long.MAX_VALUE)));
  }

  @Test
  void testIntValueExactFractionFail() {
    assertThrows(Throw.class, () -> Scalars.intValueExact(RationalScalar.of(2, 3)));
  }

  @Test
  void testLongValueExact() {
    assertEquals(Scalars.longValueExact(RealScalar.of(123)), 123);
    assertEquals(Scalars.longValueExact(RealScalar.of(123)), 123L);
    assertEquals(Scalars.longValueExact(RealScalar.of(Long.MIN_VALUE)), Long.MIN_VALUE);
    assertEquals(Scalars.longValueExact(RealScalar.of(Long.MAX_VALUE)), Long.MAX_VALUE);
  }

  @Test
  void testExample() {
    Scalar s = Scalars.fromString("(3+2)*I/(-1+4)+8-I");
    Scalar c = ComplexScalar.of(RealScalar.of(8), RationalScalar.of(2, 3));
    assertEquals(c, s);
    assertEquals(s, c);
  }

  @ParameterizedTest
  @ValueSource(strings = { //
      "(3+2)(-1+4", //
      "(3+2)(-1+4+", //
      "3+2-1+4+", //
      "3+2-1+4-", //
      "3++4", //
      "3--4", //
      "3**4", //
      "3//4", //
  })
  void testParseFail(String string) {
    assertInstanceOf(StringScalar.class, Scalars.fromString(string));
  }

  @Test
  void testDivides() {
    assertTrue(Scalars.divides(RealScalar.of(3), RealScalar.of(9)));
    assertFalse(Scalars.divides(RealScalar.of(9), RealScalar.of(3)));
    assertFalse(Scalars.divides(RealScalar.of(2), RealScalar.of(9)));
    assertTrue(Scalars.divides(RationalScalar.of(3, 7), RationalScalar.of(18, 7)));
    assertFalse(Scalars.divides(RationalScalar.of(3, 7), RationalScalar.of(8, 7)));
  }

  @Test
  void testComplex() {
    Scalar c2 = ComplexScalar.of(2, 3);
    Scalar c1 = c2.multiply(RealScalar.of(3));
    assertFalse(Scalars.divides(c1, c2));
    assertTrue(Scalars.divides(c2, c1));
  }

  @Test
  void testGaussian() {
    Scalar c1 = ComplexScalar.of(3, 1);
    Scalar c2 = ComplexScalar.of(2, -1);
    assertFalse(Scalars.divides(c1, c2));
    assertTrue(Scalars.divides(c2, c1));
  }

  @Test
  void testQuantity() {
    assertTrue(Scalars.divides(Quantity.of(3, "m"), Quantity.of(9, "m")));
    assertFalse(Scalars.divides(Quantity.of(3, "m"), Quantity.of(7, "m")));
    assertFalse(Scalars.divides(Quantity.of(7, "m"), Quantity.of(3, "m")));
  }

  @Test
  void testToStringDateObject1() {
    // System.out.println("2020-12-20T04:30".length());
    DateTime dateTime = DateTime.of(2020, 12, 20, 4, 30);
    Scalar scalar = Scalars.fromString("2020-12-20T04:30");
    assertEquals(scalar, dateTime);
  }

  @Test
  void testToStringDateObject2() {
    // System.out.println("2020-12-20T04:30".length());
    DateTime dateTime = DateTime.of(2020, 12, 20, 4, 30, 3, 125_239_876);
    Scalar scalar = Scalars.fromString("2020-12-20T04:30:03.125239876");
    assertEquals(scalar, dateTime);
  }

  @Test
  void testToStringDateObject3() {
    assertInstanceOf(StringScalar.class, Scalars.fromString("2020-12-20T04:30a"));
    assertInstanceOf(StringScalar.class, Scalars.fromString("2020-12-20T04:3"));
    assertInstanceOf(StringScalar.class, Scalars.fromString("2020-12-20T04a30"));
    assertInstanceOf(StringScalar.class, Scalars.fromString("2020.12.20T04:30"));
    assertInstanceOf(StringScalar.class, Scalars.fromString("2020-12-20R04:30"));
  }

  @Test
  void testStringIndexOf() {
    int index = "asd".indexOf('#', 10);
    assertEquals(-1, index);
  }

  @Test
  void testQuantityIncompatible() {
    Scalar qs1 = Quantity.of(6, "m");
    Scalar qs2 = Quantity.of(3, "s");
    assertThrows(Throw.class, () -> Scalars.divides(qs1, qs2));
  }

  @Test
  void testBigIntegerExactNullFail() {
    assertThrows(Throw.class, () -> Scalars.bigIntegerValueExact(null));
  }

  @Test
  void testOptionalBigIntegerNullFail() {
    assertThrows(NullPointerException.class, () -> Scalars.optionalBigInteger(null));
  }
}
