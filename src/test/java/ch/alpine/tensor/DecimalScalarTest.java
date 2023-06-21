// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Re;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;

class DecimalScalarTest {
  private static final String PI100 = "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068";

  @Test
  void testUnderDouble() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL128));
    Scalar d = s.under(Pi.TWO);
    assertInstanceOf(DoubleScalar.class, d);
    assertEquals(d, RealScalar.of(2));
    assertEquals(s.multiply(s.one()), s);
  }

  @Test
  void testUnderRational() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL128));
    Scalar d = s.under(RationalScalar.of(1, 2));
    assertInstanceOf(DecimalScalar.class, d);
    Tolerance.CHOP.requireClose(d, DoubleScalar.of(0.5 / Math.PI));
  }

  @Test
  void testUnderDecimal() {
    Scalar d1 = DecimalScalar.of(new BigDecimal("123.0123", MathContext.DECIMAL128));
    Scalar d2 = DecimalScalar.of(new BigDecimal("-11.233", MathContext.DECIMAL128));
    Scalar res = d1.under(d2);
    assertInstanceOf(DecimalScalar.class, res);
    Tolerance.CHOP.requireClose(res, DoubleScalar.of(-11.233 / 123.0123));
  }

  @Test
  void testN() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    assertEquals(N.DECIMAL64.of(s), s);
    assertInstanceOf(DoubleScalar.class, N.DOUBLE.of(s));
  }

  @Test
  void testTrig() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    Chop._06.requireZero(Sin.FUNCTION.apply(s)); // decimal32 is similiar to float
    Tolerance.CHOP.requireClose(Cos.FUNCTION.apply(s), RealScalar.ONE.negate());
  }

  @Test
  void testPower() {
    Scalar scalar = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    Scalar result = Power.of(scalar, 16);
    Scalar revers = Nest.of(Sqrt.FUNCTION, result, 4);
    assertEquals(Scalars.compare(scalar, revers), 0);
  }

  @Test
  void testPowerFail() {
    Scalar scalar = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    Scalar res = Power.of(scalar, 1682374652836L);
    assertEquals(res, DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testArg() {
    Scalar a = Arg.FUNCTION.apply(DecimalScalar.of(BigDecimal.ONE.negate()));
    Scalar b = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL128));
    Chop._20.requireClose(a, b);
  }

  private static void _checkEqCmp(Scalar s1, Scalar s2) {
    int cmp = Scalars.compare(s1, s2);
    boolean eq1 = s1.equals(s2);
    boolean eq2 = s2.equals(s1);
    assertEquals(eq1, eq2);
    assertEquals(cmp == 0, eq1);
  }

  @Test
  void testPairs() {
    Tensor vector = Tensors.of( //
        DoubleScalar.of(-0.0), //
        DoubleScalar.of(1.0 / 3.0), //
        DoubleScalar.of(2.0 / 3.0), //
        DoubleScalar.of(1.0), //
        RationalScalar.of(1, 3), //
        RationalScalar.of(2, 3), //
        DecimalScalar.of(new BigDecimal("0.33")), //
        DecimalScalar.of(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3), MathContext.DECIMAL32)), //
        DecimalScalar.of(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(3), MathContext.DECIMAL32)), //
        DecimalScalar.of(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3), MathContext.DECIMAL64)), //
        DecimalScalar.of(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(3), MathContext.DECIMAL64)), //
        DecimalScalar.of(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3), MathContext.DECIMAL128)), //
        DecimalScalar.of(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(3), MathContext.DECIMAL128)), //
        DecimalScalar.of(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3), new MathContext(50, RoundingMode.HALF_EVEN))), //
        DecimalScalar.of(BigDecimal.valueOf(2).divide(BigDecimal.valueOf(3), new MathContext(50, RoundingMode.HALF_EVEN))), //
        DecimalScalar.of(BigDecimal.ONE), //
        DecimalScalar.of(BigDecimal.ZERO), //
        RealScalar.ONE, //
        RealScalar.ZERO //
    );
    for (int i = 0; i < vector.length(); ++i)
      for (int j = 0; j < vector.length(); ++j)
        _checkEqCmp(vector.Get(i), vector.Get(j));
  }

  @Test
  void testPrecision() {
    RandomGenerator random = new Random();
    for (int value = random.nextInt(83); value < 10000; value += 83) {
      DecimalScalar decimalScalar = (DecimalScalar) DecimalScalar.of(new BigDecimal("" + Math.sqrt(value)));
      String string = decimalScalar.toString();
      Scalar dbl_s = Scalars.fromString(string);
      assertEquals(decimalScalar, dbl_s);
    }
  }

  @Test
  void testPrecisionFunc() {
    DecimalScalar scalar = (DecimalScalar) Scalars.fromString("123.123123123`50");
    assertEquals(scalar.precision(), 50);
  }

  @Test
  void testDecimalEmpty() {
    Scalar value = Scalars.fromString(" 1.1234` + 12");
    assertInstanceOf(DoubleScalar.class, value);
    assertEquals(value, RealScalar.of(13.1234));
  }

  @Test
  void testComplexEmpty() {
    Scalar value = Scalars.fromString(" 1.1567572194352718` - 1.2351191805935866` * I ");
    assertInstanceOf(ComplexScalar.class, value);
    ComplexScalar complexScalar = (ComplexScalar) value;
    assertEquals(complexScalar.real(), RealScalar.of(+1.1567572194352718));
    assertEquals(complexScalar.imag(), RealScalar.of(-1.2351191805935866));
  }

  @Test
  void testZero() {
    assertEquals(RealScalar.ZERO, DecimalScalar.of(BigDecimal.ZERO));
  }

  @Test
  void testAddMultiply() {
    BigDecimal d = BigDecimal.ONE;
    Scalar sc1 = DecimalScalar.of(d);
    Scalar sc2 = sc1.add(sc1);
    Scalar sc2c = sc1.add(sc1);
    Scalar sc4 = sc2.multiply(sc2);
    Scalar r23 = RationalScalar.of(2, 3);
    assertEquals(sc2, sc2c);
    Scalar sc4pr23 = sc4.add(r23);
    Scalar sc4mr23 = sc4.multiply(r23);
    assertInstanceOf(DecimalScalar.class, sc4pr23);
    assertInstanceOf(DecimalScalar.class, sc4mr23);
  }

  @Test
  void testDouble() {
    BigDecimal d = BigDecimal.ONE;
    Scalar sc1 = DecimalScalar.of(d);
    Scalar sc2 = sc1.add(sc1);
    Scalar sc4 = sc2.multiply(sc2);
    Scalar r23 = DoubleScalar.of(2 / 3.);
    Scalar sc4pr23 = sc4.add(r23);
    Scalar sc4mr23 = sc4.multiply(r23);
    assertInstanceOf(DoubleScalar.class, sc4pr23);
    assertInstanceOf(DoubleScalar.class, sc4mr23);
  }

  @Test
  void testReciprocal() {
    BigDecimal d = BigDecimal.ONE;
    Scalar sc1 = DecimalScalar.of(d);
    Scalar sc2 = sc1.add(sc1);
    DecimalScalar sc3 = (DecimalScalar) sc2.add(sc1);
    Scalar s13 = sc3.reciprocal();
    Scalar r13 = RationalScalar.of(1, 3);
    Scalar d13 = DoubleScalar.of(1. / 3);
    assertEquals(r13, s13);
    assertEquals(s13, r13);
    assertEquals(d13, s13);
    assertEquals(s13, d13);
  }

  @Test
  void testReciprocal2() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    DecimalScalar r = (DecimalScalar) s.reciprocal();
    assertTrue(7 <= r.number().precision());
  }

  @Test
  void testDivide() {
    BigDecimal d = BigDecimal.ONE;
    Scalar sc1 = DecimalScalar.of(d);
    Scalar sc2 = sc1.add(sc1);
    Scalar sc3 = sc2.add(sc1);
    Scalar s23 = sc2.divide(sc3);
    Scalar r23 = RationalScalar.of(2, 3);
    Scalar d23 = DoubleScalar.of(Math.nextUp(2. / 3));
    Tolerance.CHOP.requireClose(r23, s23);
    Tolerance.CHOP.requireClose(s23, r23);
    Tolerance.CHOP.requireClose(d23, s23);
    Tolerance.CHOP.requireClose(s23, d23);
  }

  @Test
  void testDivide2() {
    Scalar s = DecimalScalar.of(new BigDecimal("123.345"));
    Scalar d = s.divide(RationalScalar.of(2, 7));
    assertEquals(d.toString(), "431.7075");
  }

  @Test
  void testDivide3() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL128));
    Scalar d = s.divide(Pi.TWO);
    assertInstanceOf(DoubleScalar.class, d);
    assertEquals(d, RealScalar.of(0.5));
  }

  @Test
  void testDivideZero() {
    Scalar scalar = DecimalScalar.of(BigDecimal.ZERO);
    assertThrows(ArithmeticException.class, scalar::reciprocal);
  }

  @Test
  void testDivideZero2() {
    Scalar scalar = DecimalScalar.of(new BigDecimal("0", MathContext.UNLIMITED));
    assertThrows(ArithmeticException.class, scalar::reciprocal);
  }

  @Test
  void testSqrt() {
    // Mathematica N[Sqrt[2], 50] gives
    // ................1.4142135623730950488016887242096980785696718753769
    String expected = "1.414213562373095048801688724209698";
    Scalar sc1 = DecimalScalar.of(BigDecimal.ONE);
    DecimalScalar sc2 = (DecimalScalar) sc1.add(sc1);
    Scalar root2 = Sqrt.FUNCTION.apply(sc2);
    assertTrue(root2.toString().startsWith(expected));
  }

  @Test
  void testSqrtNeg() {
    // Mathematica N[Sqrt[2], 50] gives
    // ................1.4142135623730950488016887242096980785696718753769
    String expected = "1.414213562373095048801688724209698";
    Scalar sc1 = DecimalScalar.of(BigDecimal.ONE);
    DecimalScalar sc2 = (DecimalScalar) sc1.add(sc1).negate();
    Scalar root2 = Sqrt.FUNCTION.apply(sc2);
    assertEquals(Re.FUNCTION.apply(root2), RealScalar.ZERO);
    assertTrue(Im.FUNCTION.apply(root2).toString().startsWith(expected));
  }

  @Test
  void testZero1() {
    assertEquals(RealScalar.of(BigDecimal.ONE).hashCode(), BigDecimal.ONE.hashCode());
  }

  @Test
  void testRound() {
    assertEquals(Round.FUNCTION.apply(DecimalScalar.of(new BigDecimal("12.1"))), RealScalar.of(12));
    assertEquals(Round.FUNCTION.apply(DecimalScalar.of(new BigDecimal("12.99"))), RealScalar.of(13));
    assertEquals(Round.FUNCTION.apply(DecimalScalar.of(new BigDecimal("25"))), RealScalar.of(25));
    assertTrue(Round.FUNCTION.apply(DecimalScalar.of(new BigDecimal("12.99"))) instanceof RationalScalar);
  }

  @Test
  void testFloor() {
    assertEquals(Floor.FUNCTION.apply(DecimalScalar.of(new BigDecimal("12.99"))), RealScalar.of(12));
    assertEquals(Floor.FUNCTION.apply(DecimalScalar.of(new BigDecimal("25"))), RealScalar.of(25));
    assertTrue(Floor.FUNCTION.apply(DecimalScalar.of(new BigDecimal("12.99"))) instanceof RationalScalar);
  }

  @Test
  void testCeiling() {
    assertEquals(Ceiling.FUNCTION.apply(DecimalScalar.of(new BigDecimal("12.1"))), RealScalar.of(13));
    assertEquals(Ceiling.FUNCTION.apply(DecimalScalar.of(new BigDecimal("25"))), RealScalar.of(25));
    assertInstanceOf(RationalScalar.class, Ceiling.FUNCTION.apply(DecimalScalar.of(new BigDecimal("12.99"))));
  }

  @Test
  void testCompare0() {
    Scalar a = DecimalScalar.of(new BigDecimal("0.1"));
    Scalar b = DecimalScalar.of(new BigDecimal("0.2"));
    assertTrue(Scalars.lessThan(a, b));
    assertFalse(Scalars.lessThan(b, a));
  }

  @Test
  void testCompare1() {
    Scalar dec = DecimalScalar.of(new BigDecimal("0.1"));
    Scalar alt = DoubleScalar.of(0.01);
    assertTrue(Scalars.lessThan(alt, dec));
    assertFalse(Scalars.lessThan(dec, alt));
  }

  @Test
  void testCompare2() {
    Scalar dec = DecimalScalar.of(new BigDecimal("0.1"));
    Scalar alt = RationalScalar.of(1, 100);
    assertTrue(Scalars.lessThan(alt, dec));
    assertFalse(Scalars.lessThan(dec, alt));
  }

  @Test
  void testCompare3() {
    assertTrue(Scalars.lessThan(DecimalScalar.of(new BigDecimal("-3")), RealScalar.ZERO));
    assertFalse(Scalars.lessThan(DecimalScalar.of(new BigDecimal("3")), RealScalar.ZERO));
    assertFalse(Scalars.lessThan(RealScalar.ZERO, DecimalScalar.of(new BigDecimal("-3"))));
    assertTrue(Scalars.lessThan(RealScalar.ZERO, DecimalScalar.of(new BigDecimal("3"))));
  }

  @Test
  void testEquals() {
    Scalar rs1 = RealScalar.ONE;
    Scalar ds1 = DecimalScalar.of(new BigDecimal("1.0000"));
    assertEquals(ds1, rs1);
    assertEquals(rs1, ds1);
  }

  @Test
  void testEqualsSpecial() {
    Scalar ds1 = DecimalScalar.of(new BigDecimal("1.0234", MathContext.DECIMAL128));
    assertInstanceOf(DecimalScalar.class, ds1);
    assertNotEquals(null, ds1);
    assertNotEquals(ds1, ComplexScalar.of(1, 2));
    assertNotEquals(ds1, GaussScalar.of(6, 7));
  }

  @Test
  void testEqualsObject() {
    Object object = DecimalScalar.of(new BigDecimal("1.0234", MathContext.DECIMAL128));
    assertNotEquals("hello", object);
  }

  private static final class ObjectExtension {
    @Override
    public boolean equals(Object obj) {
      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }
  }

  @Test
  void testEqualsTrue() {
    Object object = new ObjectExtension();
    final Scalar ds1 = DecimalScalar.of(new BigDecimal("1.0234", MathContext.DECIMAL128));
    assertEquals(ds1, object);
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> DecimalScalar.of((BigDecimal) null));
  }
}
