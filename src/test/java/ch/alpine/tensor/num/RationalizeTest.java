// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.N;

class RationalizeTest {
  @Test
  void testBasics1000() {
    final Scalar max = RealScalar.of(1000);
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(max);
    // assertEquals(Rationalize.of(RealScalar.of(Math.PI), RealScalar.of(102)).toString(), "311/99");
    assertEquals(suo.apply(RealScalar.of(2 / 3.)).toString(), "2/3");
    assertEquals(suo.apply(RealScalar.of(-2 / 3.)).toString(), "-2/3");
    assertEquals(suo.apply(RealScalar.of(13)).toString(), "13");
    assertEquals(suo.apply(RealScalar.of(-13)).toString(), "-13");
    assertEquals(suo.apply(RealScalar.of(4)), RealScalar.of(4));
    assertEquals(suo.apply(RealScalar.of(-4)), RealScalar.of(-4));
    Scalar tenth = RealScalar.of(0.1);
    assertEquals(suo.apply(tenth).toString(), "1/10");
    assertEquals(suo.apply(RealScalar.of(0)).toString(), "0");
  }

  private static void betterEquals(Scalar value) {
    Scalar eps = RationalScalar.of(1, 100);
    Scalar hi = Ceiling.toMultipleOf(eps).apply(value);
    Scalar lo = Floor.toMultipleOf(eps).apply(value);
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(100);
    Scalar me = suo.apply(value);
    Scalar e1 = Abs.between(value, lo);
    Scalar e2 = Abs.between(value, hi);
    Scalar be = Abs.between(value, me);
    assertTrue(Scalars.lessEquals(be, e1));
    assertTrue(Scalars.lessEquals(be, e2));
  }

  @Test
  void testLong() {
    RandomVariate.of(UniformDistribution.of(-20, 20), 1000).stream() //
        .map(Scalar.class::cast) //
        .forEach(RationalizeTest::betterEquals);
  }

  @Test
  void testBasics5() {
    final Scalar max = RealScalar.of(5);
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(max);
    Scalar tenth = RealScalar.of(0.1);
    assertEquals(suo.apply(tenth).toString(), "1/5");
    assertEquals(suo.apply(tenth.negate()), RealScalar.ZERO);
  }

  @Test
  void testBasics4() {
    final Scalar max = RealScalar.of(4);
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(max);
    Scalar tenth = RealScalar.of(0.1);
    assertEquals(suo.apply(tenth), RealScalar.ZERO);
    assertEquals(suo.apply(tenth.negate()), RealScalar.ZERO);
  }

  @Test
  void testRational() {
    Scalar THND = RealScalar.of(1000);
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(THND);
    // final Scalar THND = RealScalar.of(1000);
    // assertEquals(Rationalize.of(RealScalar.of(Math.PI), RealScalar.of(102)).toString(), "311/99");
    assertEquals(suo.apply(RationalScalar.of(2, 3)), RationalScalar.of(2, 3));
    for (int num = 76510; num <= 76650; ++num) {
      RationalScalar input = (RationalScalar) RationalScalar.of(num, 10000);
      final Scalar result = suo.apply(input);
      if (Scalars.lessThan(THND, RealScalar.of(input.denominator()))) {
        assertFalse(input.equals(result));
        Scalar residual = N.DOUBLE.apply(input.subtract(result));
        Chop._04.requireZero(residual);
      } else {
        assertTrue(input.equals(result));
      }
    }
  }

  @Test
  void testSol1() {
    ScalarUnaryOperator suo = Rationalize.withDenominatorLessEquals(RealScalar.of(6));
    Scalar tenth = RealScalar.of(0.1);
    // double comp = 1/6.0; // 0.1666...
    // System.out.println(comp);
    assertEquals(suo.apply(tenth), RationalScalar.of(1, 6));
    assertEquals(suo.apply(tenth.negate()), RationalScalar.of(-1, 6));
  }

  @Test
  void testRoundConsistency() {
    Tensor vector = Tensors.vectorDouble(-2.5, -2, -1.5, -1, -0.5, 0, 0.1, 0.5, 1, 1.5, 2, 2.5);
    List<Long> round = vector.stream() //
        .map(RealScalar.class::cast) //
        .map(RealScalar::number) //
        .map(Number::doubleValue) //
        .map(Math::round) //
        .collect(Collectors.toList());
    Tensor ratio = vector.map(Rationalize.withDenominatorLessEquals(RealScalar.ONE));
    assertEquals(ratio, Tensors.vector(round));
  }

  private static void denCheck(Scalar scalar, Scalar max) {
    Tensor re = Rationalize.withDenominatorLessEquals(max).apply(scalar);
    RationalScalar rationalScalar = (RationalScalar) re;
    assertTrue(Scalars.lessEquals(RealScalar.of(rationalScalar.denominator()), max));
  }

  @Test
  void testDenominator() {
    Random random = new Random();
    Distribution distribution = UniformDistribution.of(-0.5, 0.5);
    for (Tensor scalar : RandomVariate.of(distribution, 100)) {
      Scalar max = RealScalar.of(random.nextInt(10_000_000));
      denCheck((Scalar) scalar, max);
    }
  }

  @Test
  void testRationalize() {
    assertEquals(Rationalize._1.apply(DoubleScalar.of(12.435)), RationalScalar.of(124, 10));
    assertEquals(Rationalize._2.apply(DoubleScalar.of(12.435)), RationalScalar.of(311, 25));
    assertEquals(Rationalize._3.apply(DoubleScalar.of(12.435)), RationalScalar.of(12435, 1000));
    assertEquals(Rationalize._4.apply(Pi.VALUE), RationalScalar.of(31416, 10000));
    assertEquals(Rationalize._3.apply(Quantity.of(1.23456, "m")), Quantity.of(RationalScalar.of(1235, 1000), "m"));
  }

  @Test
  void testFailPositive() {
    assertThrows(TensorRuntimeException.class, () -> Rationalize.withDenominatorLessEquals(RealScalar.ZERO));
  }

  @Test
  void testFailIntegerQ() {
    assertThrows(TensorRuntimeException.class, () -> Rationalize.withDenominatorLessEquals(RealScalar.of(1.23)));
  }
}
