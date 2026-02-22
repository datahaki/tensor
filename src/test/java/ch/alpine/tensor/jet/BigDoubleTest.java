package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.num.Pi;

class BigDoubleTest {
  static void assertNormalized(BigDouble x) {
    double hi = x.hi();
    double lo = x.lo();
    // lo must be tiny relative to hi (non-overlapping)
    if (Math.abs(lo) > Math.ulp(hi))
      throw new AssertionError("Not normalized: " + x);
  }

  @Test
  void testRandom() {
    BigDouble extendedDouble = new BigDouble(.2, .3);
    assertNormalized(extendedDouble);
    assertEquals(extendedDouble.toString(), "0.5");
    BigDouble bigDouble = Parse.parse("0.1");
    BigDouble res = bigDouble.add(extendedDouble);
    BigDouble s1 = res.sub(bigDouble);
    BigDouble s2 = res.add(bigDouble.negate());
    assertEquals(s1, s2);
  }

  @Test
  void testSimple() {
    assertEquals(BigDouble.ONE.toString(), "1");
    assertEquals(BigDouble.TWO.toString(), "2");
    // IO.println(BigDouble.of(1e-10).toString());
  }

  static void check(BigDouble big, BigDecimal dec) {
    String string = big.toString();
    String pattern = string.substring(0, string.length() - 1);
    String string2 = dec.stripTrailingZeros().toString();
    if (!string2.startsWith(pattern)) {
      IO.println(pattern);
      IO.println(string2);
      throw new RuntimeException();
    }
  }

  @Test
  void testPi() {
    check(BigDouble.PI, (BigDecimal) Pi.in(35).number());
  }

  @Test
  void testSin() {
    // ........ 0.9092974268256816930151652762519666
    String m = "0.909297426825681695396019865911745";
    BigDouble bd = BigDouble.TWO.sin();
    // IO.println(bd.toString());
  }

  @Test
  void testNegZero() {
    BigDouble big1 = new BigDouble(+0.0, +0.0);
    BigDouble big2 = new BigDouble(-0.0, -0.0);
    assertFalse(big1.equalsExact(big2));
    assertEquals(big1, big2);
  }

  @Test
  void testDemo() {
    BigDouble a = BigDouble.of(1e16);
    BigDouble b = BigDouble.of(1.0);
    // This fails in double:
    double broken = (1e16 + 1.0) - 1e16;
    // This works:
    BigDouble precise = a.add(b).sub(a);
    // System.out.println("double result = " + broken);
    // π via sqrt example
    BigDouble two = BigDouble.of(2.0);
    BigDouble sqrt2 = two.sqrt();
    // System.out.println("sqrt(2) ≈ " + sqrt2.toString());
    BigDouble x = BigDouble.of(1e16).add(BigDouble.of(1));
    BigDouble y = x.sub(BigDouble.of(1e16));
    // System.out.println("DoubleDouble : " + y.toBigDecimal());
    BigDecimal bd = y.toBigDecimal();
    // System.out.println("BigDecimal : " + bd.toPlainString());
  }
}
