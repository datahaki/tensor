// code by jph
package ch.alpine.tensor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;

import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.Arg;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Cos;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class DecimalScalarTest extends TestCase {
  private static final String PI100 = "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068";

  public void testUnderDouble() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL128));
    Scalar d = s.under(Pi.TWO);
    assertTrue(d instanceof DoubleScalar);
    assertEquals(d, RealScalar.of(2));
    assertEquals(s.multiply(s.one()), s);
  }

  public void testUnderRational() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL128));
    Scalar d = s.under(RationalScalar.of(1, 2));
    assertTrue(d instanceof DecimalScalar);
    Tolerance.CHOP.requireClose(d, DoubleScalar.of(0.5 / Math.PI));
  }

  public void testUnderDecimal() {
    Scalar d1 = DecimalScalar.of(new BigDecimal("123.0123", MathContext.DECIMAL128));
    Scalar d2 = DecimalScalar.of(new BigDecimal("-11.233", MathContext.DECIMAL128));
    Scalar res = d1.under(d2);
    assertTrue(res instanceof DecimalScalar);
    Tolerance.CHOP.requireClose(res, DoubleScalar.of(-11.233 / 123.0123));
  }

  public void testN() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    assertEquals(N.DECIMAL64.of(s), s);
    assertTrue(N.DOUBLE.of(s) instanceof DoubleScalar);
  }

  public void testTrig() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    Chop._06.requireZero(Sin.of(s)); // decimal32 is similiar to float
    Tolerance.CHOP.requireClose(Cos.of(s), RealScalar.ONE.negate());
  }

  public void testPower() {
    Scalar scalar = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    Scalar result = Power.of(scalar, 16);
    Scalar revers = Nest.of(Sqrt::of, result, 4);
    assertEquals(Scalars.compare(scalar, revers), 0);
  }

  public void testPowerFail() {
    Scalar scalar = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    Scalar res = Power.of(scalar, 1682374652836L);
    assertEquals(res, DoubleScalar.POSITIVE_INFINITY);
  }

  public void testArg() {
    Scalar a = Arg.of(DecimalScalar.of(BigDecimal.ONE.negate()));
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

  public void testPairs() {
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

  public void testPrecision() {
    Random random = new Random();
    for (int value = random.nextInt(83); value < 10000; value += 83) {
      DecimalScalar decimalScalar = (DecimalScalar) DecimalScalar.of(new BigDecimal("" + Math.sqrt(value)));
      String string = decimalScalar.toString();
      Scalar dbl_s = Scalars.fromString(string);
      assertEquals(decimalScalar, dbl_s);
    }
  }

  public void testPrecisionFunc() {
    DecimalScalar scalar = (DecimalScalar) Scalars.fromString("123.123123123`50");
    assertEquals(scalar.precision(), 50);
  }

  public void testDecimalEmpty() {
    Scalar value = Scalars.fromString(" 1.1234` + 12");
    assertTrue(value instanceof DoubleScalar);
    assertEquals(value, RealScalar.of(13.1234));
  }

  public void testComplexEmpty() {
    Scalar value = Scalars.fromString(" 1.1567572194352718` - 1.2351191805935866` * I ");
    assertTrue(value instanceof ComplexScalar);
    ComplexScalar complexScalar = (ComplexScalar) value;
    assertEquals(complexScalar.real(), RealScalar.of(+1.1567572194352718));
    assertEquals(complexScalar.imag(), RealScalar.of(-1.2351191805935866));
  }

  public void testZero() {
    assertEquals(RealScalar.ZERO, DecimalScalar.of(BigDecimal.ZERO));
  }

  public void testAddMultiply() {
    BigDecimal d = BigDecimal.ONE;
    Scalar sc1 = DecimalScalar.of(d);
    Scalar sc2 = sc1.add(sc1);
    Scalar sc2c = sc1.add(sc1);
    Scalar sc4 = sc2.multiply(sc2);
    Scalar r23 = RationalScalar.of(2, 3);
    assertEquals(sc2, sc2c);
    Scalar sc4pr23 = sc4.add(r23);
    Scalar sc4mr23 = sc4.multiply(r23);
    assertTrue(sc4pr23 instanceof DecimalScalar);
    assertTrue(sc4mr23 instanceof DecimalScalar);
  }

  public void testDouble() {
    BigDecimal d = BigDecimal.ONE;
    Scalar sc1 = DecimalScalar.of(d);
    Scalar sc2 = sc1.add(sc1);
    Scalar sc4 = sc2.multiply(sc2);
    Scalar r23 = DoubleScalar.of(2 / 3.);
    Scalar sc4pr23 = sc4.add(r23);
    Scalar sc4mr23 = sc4.multiply(r23);
    assertTrue(sc4pr23 instanceof DoubleScalar);
    assertTrue(sc4mr23 instanceof DoubleScalar);
  }

  public void testReciprocal() {
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

  public void testReciprocal2() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL32));
    DecimalScalar r = (DecimalScalar) s.reciprocal();
    assertTrue(7 <= r.number().precision());
  }

  public void testDivide() {
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

  public void testDivide2() {
    Scalar s = DecimalScalar.of(new BigDecimal("123.345"));
    Scalar d = s.divide(RationalScalar.of(2, 7));
    assertEquals(d.toString(), "431.7075");
  }

  public void testDivide3() {
    Scalar s = DecimalScalar.of(new BigDecimal(PI100, MathContext.DECIMAL128));
    Scalar d = s.divide(Pi.TWO);
    assertTrue(d instanceof DoubleScalar);
    assertEquals(d, RealScalar.of(0.5));
  }

  public void testSqrt() {
    // Mathematica N[Sqrt[2], 50] gives
    // ................1.4142135623730950488016887242096980785696718753769
    String expected = "1.414213562373095048801688724209698";
    Scalar sc1 = DecimalScalar.of(BigDecimal.ONE);
    DecimalScalar sc2 = (DecimalScalar) sc1.add(sc1);
    Scalar root2 = Sqrt.FUNCTION.apply(sc2);
    assertTrue(root2.toString().startsWith(expected));
  }

  public void testSqrtNeg() {
    // Mathematica N[Sqrt[2], 50] gives
    // ................1.4142135623730950488016887242096980785696718753769
    String expected = "1.414213562373095048801688724209698";
    Scalar sc1 = DecimalScalar.of(BigDecimal.ONE);
    DecimalScalar sc2 = (DecimalScalar) sc1.add(sc1).negate();
    Scalar root2 = Sqrt.FUNCTION.apply(sc2);
    assertEquals(Real.of(root2), RealScalar.ZERO);
    assertTrue(Imag.of(root2).toString().startsWith(expected));
  }

  public void testZero1() {
    assertEquals(RealScalar.of(BigDecimal.ONE).hashCode(), BigDecimal.ONE.hashCode());
  }

  public void testRound() {
    assertEquals(Round.of(DecimalScalar.of(new BigDecimal("12.1"))), RealScalar.of(12));
    assertEquals(Round.of(DecimalScalar.of(new BigDecimal("12.99"))), RealScalar.of(13));
    assertEquals(Round.of(DecimalScalar.of(new BigDecimal("25"))), RealScalar.of(25));
    assertTrue(Round.of(DecimalScalar.of(new BigDecimal("12.99"))) instanceof RationalScalar);
  }

  public void testFloor() {
    assertEquals(Floor.of(DecimalScalar.of(new BigDecimal("12.99"))), RealScalar.of(12));
    assertEquals(Floor.of(DecimalScalar.of(new BigDecimal("25"))), RealScalar.of(25));
    assertTrue(Floor.of(DecimalScalar.of(new BigDecimal("12.99"))) instanceof RationalScalar);
  }

  public void testCeiling() {
    assertEquals(Ceiling.of(DecimalScalar.of(new BigDecimal("12.1"))), RealScalar.of(13));
    assertEquals(Ceiling.of(DecimalScalar.of(new BigDecimal("25"))), RealScalar.of(25));
    assertTrue(Ceiling.of(DecimalScalar.of(new BigDecimal("12.99"))) instanceof RationalScalar);
  }

  public void testCompare0() {
    Scalar a = DecimalScalar.of(new BigDecimal("0.1"));
    Scalar b = DecimalScalar.of(new BigDecimal("0.2"));
    assertTrue(Scalars.lessThan(a, b));
    assertFalse(Scalars.lessThan(b, a));
  }

  public void testCompare1() {
    Scalar dec = DecimalScalar.of(new BigDecimal("0.1"));
    Scalar alt = DoubleScalar.of(0.01);
    assertTrue(Scalars.lessThan(alt, dec));
    assertFalse(Scalars.lessThan(dec, alt));
  }

  public void testCompare2() {
    Scalar dec = DecimalScalar.of(new BigDecimal("0.1"));
    Scalar alt = RationalScalar.of(1, 100);
    assertTrue(Scalars.lessThan(alt, dec));
    assertFalse(Scalars.lessThan(dec, alt));
  }

  public void testCompare3() {
    assertTrue(Scalars.lessThan(DecimalScalar.of(new BigDecimal("-3")), RealScalar.ZERO));
    assertFalse(Scalars.lessThan(DecimalScalar.of(new BigDecimal("3")), RealScalar.ZERO));
    assertFalse(Scalars.lessThan(RealScalar.ZERO, DecimalScalar.of(new BigDecimal("-3"))));
    assertTrue(Scalars.lessThan(RealScalar.ZERO, DecimalScalar.of(new BigDecimal("3"))));
  }

  public void testEquals() {
    Scalar rs1 = RealScalar.ONE;
    Scalar ds1 = DecimalScalar.of(new BigDecimal("1.0000"));
    assertEquals(ds1, rs1);
    assertEquals(rs1, ds1);
  }

  public void testEqualsSpecial() {
    Scalar ds1 = DecimalScalar.of(new BigDecimal("1.0234", MathContext.DECIMAL128));
    assertTrue(ds1 instanceof DecimalScalar);
    assertFalse(ds1.equals(null));
    assertFalse(ds1.equals(ComplexScalar.of(1, 2)));
    assertFalse(ds1.equals(GaussScalar.of(6, 7)));
  }

  public void testEqualsObject() {
    Object object = DecimalScalar.of(new BigDecimal("1.0234", MathContext.DECIMAL128));
    assertFalse(object.equals("hello"));
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

  public void testEqualsTrue() {
    Object object = new ObjectExtension();
    final Scalar ds1 = DecimalScalar.of(new BigDecimal("1.0234", MathContext.DECIMAL128));
    assertTrue(ds1.equals(object));
  }

  public void testNullFail() {
    AssertFail.of(() -> DecimalScalar.of((BigDecimal) null));
  }
}
