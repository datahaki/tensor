// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.chq.ExactScalarQ;

class ScalarParserTest {
  @Test
  void testDouble() {
    assertEquals(ScalarParser.of("3.14`30.123"), ScalarParser.of("3.14"));
  }

  @Test
  void testComplex() {
    Scalar scalar = ScalarParser.of("3.14`30.123+2.12`99.322*I");
    assertEquals(scalar, ComplexScalar.of(3.14, 2.12));
  }

  @Test
  void testDivisions() {
    Scalar scalar = ScalarParser.of("40*3/4/5/6");
    assertEquals(scalar, ComplexScalar.of(1, 0));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testMix() {
    Scalar scalar = ScalarParser.of("80*3/4/2*5/6");
    assertEquals(scalar, ComplexScalar.of(25, 0));
    ExactScalarQ.require(scalar);
  }

  @Test
  void testImagFormat() {
    assertEquals(ScalarParser.imagToString(RealScalar.of(2.13)), "2.13*I");
    assertEquals(ScalarParser.imagToString(RealScalar.of(-2.13)), "-2.13*I");
  }

  @Test
  void testImagFormatI() {
    assertEquals(ScalarParser.imagToString(RealScalar.ONE), "I");
    assertEquals(ScalarParser.imagToString(RealScalar.ONE.negate()), "-I");
  }

  @Test
  void testRational1() {
    Scalar z = ComplexScalar.of(RationalScalar.of(1, 2), RationalScalar.of(1, 3));
    assertEquals(Scalars.fromString(z.toString()), z);
  }

  @Test
  void testRational2() {
    Scalar z = ComplexScalar.of(RationalScalar.of(1, 2), RationalScalar.of(-1, 3));
    assertEquals(Scalars.fromString(z.toString()), z);
  }

  @Test
  void testDecimal() {
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DecimalScalar.of(new BigDecimal("1"))).toString(), "I");
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DecimalScalar.of(new BigDecimal("-1"))).toString(), "-I");
  }

  @Test
  void testDoubleImag() {
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DoubleScalar.of(1)).toString(), "1.0*I");
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DoubleScalar.of(-1)).toString(), "-1.0*I");
  }

  @Test
  void testBrackets() {
    Scalar s1 = ScalarParser.of(" (1)+(2)");
    Scalar s2 = ScalarParser.of(" ((3))+0");
    Scalar s3 = ScalarParser.of(" (1+2) ");
    Scalar s4 = ScalarParser.of(" 1+(2) ");
    assertEquals(s1, RealScalar.of(3));
    assertEquals(s1, s2);
    assertEquals(s2, s3);
    assertEquals(s3, s4);
  }

  @Test
  void testTrimVsStrip() {
    String string = "    asdb  \u0000 ";
    assertEquals(string.trim().length(), 4);
    assertEquals(string.strip().length(), 7);
  }

  @Test
  void testIsBlank() {
    String string = "    \u0000 ";
    assertTrue(string.trim().isEmpty());
    assertFalse(string.isBlank());
  }

  @Test
  void testAbcFail() {
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("(3"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("3)"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("314abc34"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("314abc"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("abc34"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("3e0.1"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("3e.1"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("3e-0.1"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("3e.-0.1"));
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("(3)(5)"));
  }

  @Test
  void testQuantityFail() {
    assertThrows(IllegalArgumentException.class, () -> ScalarParser.of("3.14[m]"));
  }

  @Test
  void testVisibility() {
    assertEquals(ScalarParser.class.getModifiers() & 1, 0);
  }
}
