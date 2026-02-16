// code by jph
package ch.alpine.tensor.sca.pow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Rationalize;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Im;
import ch.alpine.tensor.sca.Re;

class SqrtTest {
  @Test
  void testNegative() {
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(RealScalar.of(10000));
    Scalar n2 = RealScalar.of(-2);
    Scalar sr = Sqrt.FUNCTION.apply(n2);
    assertEquals(suo.apply(AbsSquared.FUNCTION.apply(sr)), RealScalar.of(2));
    assertEquals(suo.apply(sr.multiply(sr)), n2);
  }

  @Test
  void testMixingTemplates() {
    {
      Scalar tensor = RealScalar.of(-2);
      Sqrt.FUNCTION.apply(tensor);
      Scalar scalar = Sqrt.FUNCTION.apply(tensor);
      scalar.zero();
    }
    {
      Scalar tensor = Rational.of(-2, 3);
      Sqrt.FUNCTION.apply(tensor);
      Scalar scalar = Sqrt.FUNCTION.apply(tensor);
      scalar.zero();
    }
  }

  @Test
  void testComplex() {
    Scalar scalar = ComplexScalar.of(0, 2);
    Scalar root = Sqrt.FUNCTION.apply(scalar);
    Scalar res = ComplexScalar.of(1, 1);
    Tolerance.CHOP.requireClose(root, res);
  }

  @Test
  void testZero() {
    assertEquals(RealScalar.ZERO, Sqrt.FUNCTION.apply(RealScalar.ZERO));
    assertEquals(RealScalar.ZERO, Sqrt.series(RealScalar.ZERO));
  }

  @Test
  void testRational() {
    assertEquals(Sqrt.FUNCTION.apply(Rational.of(16, 25)).toString(), "4/5");
    Scalar scalar = Sqrt.FUNCTION.apply(Rational.of(-16, 25));
    assertInstanceOf(ComplexScalar.class, scalar);
    assertEquals(scalar.toString(), "4/5*I");
  }

  @Test
  void testReal() {
    assertEquals(Sqrt.FUNCTION.apply(Rational.of(-16, 25)).toString(), "4/5*I");
    assertEquals(Sqrt.FUNCTION.apply(RealScalar.of(16 / 25.)), Scalars.fromString("4/5"));
    assertEquals(Sqrt.FUNCTION.apply(RealScalar.of(-16 / 25.)), Scalars.fromString("4/5*I"));
  }

  @Test
  void testTensor() {
    Tensor vector = Tensors.vector(1, 4, 9, 16).maps(Sqrt.FUNCTION);
    assertEquals(vector, Tensors.vector(1, 2, 3, 4));
  }

  @Test
  void testPositiveInfty() {
    assertEquals( //
        Sqrt.FUNCTION.apply(DoubleScalar.POSITIVE_INFINITY), //
        DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testNegativeInfty() {
    assertEquals( //
        Sqrt.FUNCTION.apply(DoubleScalar.NEGATIVE_INFINITY), //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY));
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(9, "m^2");
    Scalar qs2 = Quantity.of(3, "m");
    assertEquals(Sqrt.FUNCTION.apply(qs1), qs2);
  }

  @Test
  void testQuantity2() {
    Scalar qs1 = Quantity.of(9, "m*s^2");
    Scalar qs2 = Quantity.of(3, "m^1/2*s");
    assertEquals(Sqrt.FUNCTION.apply(qs1), qs2);
  }

  @Test
  void testNaN() {
    assertEquals(Sqrt.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
  }

  @RepeatedTest(10)
  void testDoubleDec() {
    Scalar s1 = RandomVariate.of(UniformDistribution.of(0, 10));
    Scalar s2 = DecimalScalar.of(new BigDecimal(s1.toString()));
    Scalar r1 = Sqrt.FUNCTION.apply(s1);
    Scalar r2 = Sqrt.series(s2);
    Tolerance.CHOP.requireClose(r1, r2);
  }

  @Test
  void testBlock() {
    Scalar s1 = DecimalScalar.of(new BigDecimal("7.64158148097664"));
    Sqrt.series(s1);
  }

  @Test
  void testSqrtTwo() {
    // difficult to start with "2" and ask for 100 digits
    BigDecimal bd1 = new BigDecimal("2.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        new MathContext(100, RoundingMode.HALF_EVEN));
    DecimalScalar ds1 = (DecimalScalar) DecimalScalar.of(bd1);
    Scalar rt1 = Sqrt.FUNCTION.apply(ds1);
    // mathematica N[Sqrt[2], 100] gives
    String m = "1.414213562373095048801688724209698078569671875376948073176679737990732478462107038850387534327641573";
    assertEquals(rt1.toString().substring(0, 70), m.substring(0, 70));
  }

  @Test
  void testSqrtNTwo() {
    // difficult to start with "2" and ask for 100 digits
    BigDecimal bd1 = new BigDecimal("-2.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
        new MathContext(100, RoundingMode.HALF_EVEN));
    DecimalScalar ds1 = (DecimalScalar) DecimalScalar.of(bd1);
    Scalar rt1 = Sqrt.FUNCTION.apply(ds1);
    assertInstanceOf(ComplexScalar.class, rt1);
    // mathematica N[Sqrt[2], 100] gives
    String m = "1.414213562373095048801688724209698078569671875376948073176679737990732478462107038850387534327641573";
    assertEquals(Im.FUNCTION.apply(rt1).toString().substring(0, 70), m.substring(0, 70));
    assertTrue(Scalars.isZero(Re.FUNCTION.apply(rt1)));
  }

  @Test
  void testSqrt() {
    String string = "29.1373503383756545452223278558123399996876";
    BigDecimal b = new BigDecimal(string);
    assertEquals(b.precision(), 42);
    Scalar sqrt = Sqrt.series(DecimalScalar.of(b));
    Scalar r1 = sqrt.multiply(sqrt);
    assertEquals(r1.toString().substring(0, string.length()), string);
  }

  @Test
  void testFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(Throw.class, () -> Sqrt.FUNCTION.apply(scalar));
  }
}
