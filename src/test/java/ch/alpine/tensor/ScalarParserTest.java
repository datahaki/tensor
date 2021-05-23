// code by jph
package ch.alpine.tensor;

import java.math.BigDecimal;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ScalarParserTest extends TestCase {
  public void testDouble() {
    assertEquals(ScalarParser.of("3.14`30.123"), ScalarParser.of("3.14"));
  }

  public void testComplex() {
    Scalar scalar = ScalarParser.of("3.14`30.123+2.12`99.322*I");
    assertEquals(scalar, ComplexScalar.of(3.14, 2.12));
  }

  public void testDivisions() {
    Scalar scalar = ScalarParser.of("40*3/4/5/6");
    assertEquals(scalar, ComplexScalar.of(1, 0));
    ExactScalarQ.require(scalar);
  }

  public void testMix() {
    Scalar scalar = ScalarParser.of("80*3/4/2*5/6");
    assertEquals(scalar, ComplexScalar.of(25, 0));
    ExactScalarQ.require(scalar);
  }

  public void testImagFormat() {
    assertEquals(ScalarParser.imagToString(RealScalar.of(2.13)), "2.13*I");
    assertEquals(ScalarParser.imagToString(RealScalar.of(-2.13)), "-2.13*I");
  }

  public void testImagFormatI() {
    assertEquals(ScalarParser.imagToString(RealScalar.ONE), "I");
    assertEquals(ScalarParser.imagToString(RealScalar.ONE.negate()), "-I");
  }

  public void testRational1() {
    Scalar z = ComplexScalar.of(RationalScalar.of(1, 2), RationalScalar.of(1, 3));
    assertEquals(Scalars.fromString(z.toString()), z);
  }

  public void testRational2() {
    Scalar z = ComplexScalar.of(RationalScalar.of(1, 2), RationalScalar.of(-1, 3));
    assertEquals(Scalars.fromString(z.toString()), z);
  }

  public void testDecimal() {
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DecimalScalar.of(new BigDecimal("1"))).toString(), "I");
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DecimalScalar.of(new BigDecimal("-1"))).toString(), "-I");
  }

  public void testDoubleImag() {
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DoubleScalar.of(1)).toString(), "1.0*I");
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DoubleScalar.of(-1)).toString(), "-1.0*I");
  }

  public void testBrackets() {
    Scalar s1 = ScalarParser.of("(1)+(2)");
    Scalar s2 = ScalarParser.of("((3))+0");
    assertEquals(s1, s2);
    assertEquals(s1, RealScalar.of(3));
  }

  public void testTrimVsStrip() {
    String string = "    asdb  \u0000 ";
    assertEquals(string.trim().length(), 4);
    assertEquals(string.strip().length(), 7);
  }

  public void testIsBlank() {
    String string = "    \u0000 ";
    assertTrue(string.trim().isEmpty());
    assertFalse(string.isBlank());
  }

  public void testAbcFail() {
    AssertFail.of(() -> ScalarParser.of("(3"));
    AssertFail.of(() -> ScalarParser.of("3)"));
    AssertFail.of(() -> ScalarParser.of("314abc34"));
    AssertFail.of(() -> ScalarParser.of("314abc"));
    AssertFail.of(() -> ScalarParser.of("abc34"));
    AssertFail.of(() -> ScalarParser.of("3e0.1"));
    AssertFail.of(() -> ScalarParser.of("3e.1"));
    AssertFail.of(() -> ScalarParser.of("3e-0.1"));
    AssertFail.of(() -> ScalarParser.of("3e.-0.1"));
  }

  public void testQuantityFail() {
    AssertFail.of(() -> ScalarParser.of("3.14[m]"));
  }
}
