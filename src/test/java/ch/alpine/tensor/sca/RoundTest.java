// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.qty.Quantity;

class RoundTest {
  @Test
  void testDouble() {
    assertEquals(Round.FUNCTION.apply(DoubleScalar.of(11.3)), DoubleScalar.of(11));
    assertEquals(Round.FUNCTION.apply(DoubleScalar.of(11.5)), DoubleScalar.of(12));
    assertEquals(Round.FUNCTION.apply(DoubleScalar.of(-11.3)), DoubleScalar.of(-11));
    assertEquals(Round.FUNCTION.apply(DoubleScalar.of(-11.5)), DoubleScalar.of(-12)); // inconsistent with Math::round
    assertEquals(Round.FUNCTION.apply(DoubleScalar.of(-11.6)), DoubleScalar.of(-12));
  }

  @Test
  void testLarge1() {
    BigInteger bigInteger = BigDecimal.valueOf(-999.5).setScale(0, RoundingMode.HALF_UP).toBigIntegerExact();
    assertEquals(bigInteger.toString(), "-1000");
  }

  @Test
  void testLarge2() {
    BigInteger bigInteger = BigDecimal.valueOf(1e30).setScale(0, RoundingMode.HALF_UP).toBigIntegerExact();
    assertEquals(bigInteger.toString(), "1000000000000000000000000000000");
  }

  @Test
  void testRational1() {
    Scalar s = RationalScalar.of(234534584545L, 13423656767L); // 17.4717
    assertEquals(Round.intValueExact(s), 17);
    assertEquals(Round.longValueExact(s), 17);
    Scalar r = Round.of(s);
    assertEquals(r, RealScalar.of(17));
    assertInstanceOf(RationalScalar.class, r);
  }

  @Test
  void testIntExactValueFail() {
    assertThrows(Throw.class, () -> Round.intValueExact(Quantity.of(1.2, "h")));
    assertThrows(Throw.class, () -> Round.longValueExact(Quantity.of(2.3, "h*s")));
  }

  @Test
  void testRational2() {
    Scalar s = RationalScalar.of(734534584545L, 13423656767L); // 54.7194
    Scalar r = Round.of(s);
    assertEquals(r, RealScalar.of(55));
    assertInstanceOf(RationalScalar.class, r);
  }

  @Test
  void testLarge() {
    BigInteger bi = new BigInteger("97826349587623498756234545976");
    Scalar s = RealScalar.of(bi);
    Scalar r = Round.of(s);
    assertInstanceOf(RationalScalar.class, r);
    assertEquals(s, r);
  }

  @Test
  void testMatsim() {
    Scalar e = DoubleScalar.of(Math.exp(1));
    Scalar b = e.multiply(RealScalar.of(new BigInteger("1000000000000000000000000000000000")));
    Scalar r = Round.of(b);
    assertEquals(r.toString().length(), "2718281828459045300000000000000000".length());
    assertTrue(r.toString().startsWith("2718281828459045"));
    assertTrue(r.toString().endsWith("00000000000000000"));
    // aarch64: "2718281828459045300000000000000000"
    // x86_64 : "2718281828459045000000000000000000"
  }

  @Test
  void testToMultipleOf1() {
    Scalar s = DoubleScalar.of(3.37151617);
    Scalar sr = Round.toMultipleOf(DecimalScalar.of(new BigDecimal("0.1"))).apply(s);
    assertEquals(sr.toString(), "3.4");
  }

  @Test
  void testToMultipleOf2() {
    Scalar s = DoubleScalar.of(3.37151617);
    Scalar sr = Round.toMultipleOf(RationalScalar.of(1, 2)).apply(s);
    assertEquals(sr.toString(), "7/2");
  }

  @Test
  void testMultiple() {
    Scalar w = Quantity.of(2, "K");
    ScalarUnaryOperator suo = Round.toMultipleOf(w);
    assertEquals(suo.apply(Quantity.of(3.9, "K")), w.multiply(RealScalar.of(2)));
    assertEquals(suo.apply(Quantity.of(-2, "K")), w.negate());
    assertEquals(suo.apply(Quantity.of(-2.1, "K")), w.multiply(RealScalar.of(-1)));
    assertEquals(suo.apply(Quantity.of(-3.9, "K")), w.multiply(RealScalar.of(-2)));
  }

  @Test
  void testRoundOptions() {
    Scalar pi = DoubleScalar.of(Math.PI);
    assertEquals(pi.map(Round._1).toString(), "3.1");
    assertEquals(pi.map(Round._2).toString(), "3.14");
    assertEquals(pi.map(Round._3).toString(), "3.142");
    assertEquals(pi.map(Round._4).toString(), "3.1416");
    assertEquals(pi.map(Round._5).toString(), "3.14159");
    assertEquals(pi.map(Round._6).toString(), "3.141593");
    assertEquals(pi.map(Round._7).toString(), "3.1415927");
    assertEquals(pi.map(Round._8).toString(), "3.14159265");
    assertEquals(pi.map(Round._9).toString(), "3.141592654");
  }

  @Test
  void testRoundOptions2() {
    Scalar pi = Scalars.fromString("3.100000000000008");
    assertEquals(pi.map(Round._1).toString(), "3.1");
    assertEquals(pi.map(Round._2).toString(), "3.10");
    assertEquals(pi.map(Round._3).toString(), "3.100");
    assertEquals(pi.map(Round._4).toString(), "3.1000");
    assertEquals(pi.map(Round._5).toString(), "3.10000");
    assertEquals(pi.map(Round._6).toString(), "3.100000");
    assertEquals(pi.map(Round._7).toString(), "3.1000000");
    assertEquals(pi.map(Round._8).toString(), "3.10000000");
    assertEquals(pi.map(Round._9).toString(), "3.100000000");
  }

  @Test
  void testParsing() {
    Scalar scalar = ComplexScalar.of(RealScalar.of(2.3), RationalScalar.of(5, 8));
    assertEquals(Round.FUNCTION.apply(scalar), ComplexScalar.of(2, 1));
    assertEquals(Ceiling.FUNCTION.apply(scalar), ComplexScalar.of(3, 1));
    assertEquals(Floor.FUNCTION.apply(scalar), ComplexScalar.of(2, 0));
  }

  @Test
  void testRoundOptions3() {
    Scalar pi = (Scalar) Scalars.fromString("1234.100000000000008").map(Round._2);
    DecimalScalar ds = (DecimalScalar) pi;
    BigDecimal bd = ds.number();
    assertEquals(bd.precision(), 4 + 2);
  }

  @Test
  void testPrecision() {
    Scalar pi = DecimalScalar.of(new BigDecimal("1234.10"));
    DecimalScalar ds = (DecimalScalar) pi;
    BigDecimal bd = ds.number();
    assertEquals(bd.precision(), 4 + 2);
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(2.333, "m");
    Scalar qs2 = Quantity.of(2, "m");
    assertEquals(Round.of(qs1), qs2);
  }

  @Test
  void testNonFailInfPos() {
    Scalar scalar = DoubleScalar.POSITIVE_INFINITY;
    assertEquals(Round.of(scalar), scalar);
  }

  @Test
  void testNonFailInfNeg() {
    Scalar scalar = DoubleScalar.NEGATIVE_INFINITY;
    assertEquals(Round.of(scalar), scalar);
  }

  @Test
  void testNonFailNaN() {
    assertTrue(Double.isNaN(Round.of(DoubleScalar.INDETERMINATE).number().doubleValue()));
    assertTrue(Double.isNaN(Round._2.apply(DoubleScalar.INDETERMINATE).number().doubleValue()));
  }

  @Test
  void testTypeFail() {
    assertThrows(Throw.class, () -> Round.of(StringScalar.of("some")));
  }
}
