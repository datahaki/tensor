// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.Inverse;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.mat.re.Pivots;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.Expectation;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

class AroundTest {
  @Test
  void testZeroDropSigma() {
    assertEquals(Around.of(4, 0), RealScalar.of(4));
  }

  @Test
  void testPlusGaussian() {
    Scalar a = Around.of(10, 3);
    Scalar b = Around.of(-2, 4);
    Scalar c = a.add(b);
    assertEquals(c, Around.of(8, 5));
  }

  @Test
  void testPlusReal() {
    Scalar a = Around.of(10, 1);
    Scalar b = RealScalar.of(3);
    Scalar c = a.add(b);
    assertEquals(c, Around.of(13, 1));
  }

  @Test
  void testMultiply() {
    Scalar a = Around.of(5, 2);
    Scalar b = RealScalar.of(-3);
    Scalar c = a.multiply(b);
    assertEquals(c, Around.of(-15, 2 * 3));
  }

  @Test
  void testMultiplyAround() {
    Scalar a = Around.of(7, 2);
    Scalar b = Around.of(-4, 3);
    Around c = (Around) a.multiply(b);
    Chop._06.requireClose(c.mean(), RealScalar.of(-28));
    Chop._06.requireClose(c.uncertainty(), RealScalar.of(22.472205054));
  }

  @Test
  void testNegate() {
    Scalar b = Around.of(-4, 3);
    ExactScalarQ.require(b);
    assertEquals(b.negate(), Around.of(4, 3));
  }

  @Test
  void testMean() {
    Tensor vector = Tensors.of(Around.of(2, 3), Around.of(3, 1), Around.of(-3, 1));
    Scalar mean = Mean.ofVector(vector);
    assertInstanceOf(Around.class, mean);
    Scalar actual = Around.of(Scalars.fromString("2/3"), RealScalar.of(1.1055415967851332));
    assertEquals(mean, actual);
  }

  @Test
  void testNonExact() {
    assertTrue(ExactScalarQ.of(Around.of(1, 2)));
    assertFalse(ExactScalarQ.of(Around.of(1, 0.2)));
    assertFalse(ExactScalarQ.of(Around.of(0.3, 2)));
    assertFalse(ExactScalarQ.of(Around.of(0.3, 0.5)));
  }

  @Test
  void testGaussianWithQuantity() {
    Scalar gq1 = Around.of( //
        Quantity.of(10, "m"), //
        Quantity.of(3, "m"));
    Scalar gq2 = Around.of( //
        Quantity.of(-3, "m"), //
        Quantity.of(4, "m"));
    Scalar gq3 = gq1.add(gq2);
    Scalar ga3 = Around.of( //
        Quantity.of(7, "m"), //
        Quantity.of(5, "m"));
    assertEquals(gq3, ga3);
    Scalar qs = Quantity.of(7, "s");
    Scalar gq4 = gq1.multiply(qs);
    Scalar ga4 = Around.of( //
        Quantity.of(70, "m*s"), //
        Quantity.of(21, "m*s"));
    assertEquals(gq4, ga4);
  }

  @Test
  void testDistribution() {
    Around around = (Around) Around.of(-200, 0.8);
    Distribution distribution = around.distribution();
    Random random = new Random(1);
    Scalar mean = Mean.ofVector(RandomVariate.of(distribution, random, 20));
    Chop.below(3).requireClose(mean, RealScalar.of(-200));
  }

  @Test
  void testDistWithQuantity() {
    Around gq1 = (Around) Around.of( //
        Quantity.of(3, "m"), //
        Quantity.of(2, "m"));
    ExactScalarQ.require(gq1);
    Distribution distribution = gq1.distribution(); // operates on Quantity
    Scalar rand = RandomVariate.of(distribution); // produces quantity with [m]
    assertInstanceOf(Quantity.class, rand);
    assertEquals(Expectation.mean(distribution), Quantity.of(3, "m"));
    assertEquals(gq1.one(), RealScalar.ONE);
    assertEquals(gq1.one().multiply(gq1), gq1);
    Tolerance.CHOP.requireClose( // exact would be nice
        Expectation.variance(distribution), Quantity.of(4, "m^2"));
  }

  @Test
  void testZero() {
    Scalar scalar = Around.of( //
        Quantity.of(3, "m"), //
        Quantity.of(2, "m"));
    assertEquals(scalar.zero(), Quantity.of(0, "m"));
    assertEquals(scalar.one(), RealScalar.ONE);
    scalar.hashCode();
    Scalar b = Around.of( //
        Quantity.of(3, "m"), //
        Quantity.of(3, "m"));
    assertFalse(scalar.equals(b));
    Scalar a = Around.of( //
        Quantity.of(2, "m"), //
        Quantity.of(2, "m"));
    assertFalse(scalar.equals(a));
  }

  @Test
  void testReciprocal() {
    Scalar scalar = Around.of( //
        Quantity.of(-3, "s"), //
        Quantity.of(7, "s")).reciprocal();
    assertEquals(scalar, Around.of( //
        Quantity.of(RationalScalar.of(-1, 3), "s^-1"), //
        Quantity.of(RationalScalar.of(7, 9), "s^-1")));
    assertFalse(scalar.equals(Pi.VALUE));
    N.DOUBLE.of(scalar);
    N.DECIMAL64.of(scalar);
  }

  private static Tensor _meanOnly(Tensor tensor) {
    return tensor.map(s -> ((Around) s).mean());
  }

  @Test
  void testMultiplySymmetric() {
    Scalar a = Around.of( //
        Quantity.of(-3, "s"), //
        Quantity.of(7, "s"));
    Scalar b = a.reciprocal();
    Scalar c1 = a.multiply(b);
    Scalar c2 = b.multiply(a);
    assertEquals(c1, c2);
    Scalar factor = RationalScalar.of(7, 9);
    assertEquals(a.multiply(factor), factor.multiply(a));
  }

  @Test
  void testGaussianElimination() {
    Tensor matrix = Tensors.matrix(new Scalar[][] { //
        { Around.of(Quantity.of(-3, "s"), Quantity.of(2, "s")), Around.of(Quantity.of(3, "s"), Quantity.of(1, "s")) }, //
        { Around.of(Quantity.of(1, "s"), Quantity.of(1, "s")), Around.of(Quantity.of(1, "s"), Quantity.of(4, "s")) } });
    Tensor sol = LinearSolve.of(matrix, matrix.get(Tensor.ALL, 0), Pivots.FIRST_NON_ZERO);
    assertEquals(_meanOnly(sol), UnitVector.of(2, 0));
    Tensor inverse = Inverse.of(matrix);
    Tensor c1 = matrix.dot(inverse);
    Tensor c2 = inverse.dot(matrix);
    assertEquals(_meanOnly(c1), IdentityMatrix.of(2));
    assertEquals(_meanOnly(c2), IdentityMatrix.of(2));
  }

  @Test
  void testFail() {
    assertThrows(TensorRuntimeException.class, () -> Around.of(2, -3));
  }

  @Test
  void testNumberFail() {
    Scalar scalar = Around.of(2, 3);
    assertEquals(scalar.toString(), "2\u00B13");
    assertThrows(TensorRuntimeException.class, () -> scalar.number());
  }

  @Test
  void testAbsComplex() {
    Scalar scalar = Around.of(ComplexScalar.of(3, 4), RealScalar.ONE);
    Scalar abs = Abs.FUNCTION.apply(scalar);
    assertEquals(abs, Around.of(5, 1));
  }

  @Test
  void testSpecialCase() {
    assertThrows(TensorRuntimeException.class, () -> Around.of(Quantity.of(1, "m"), RealScalar.ZERO));
  }

  @Test
  void testSqrt() {
    Around around = (Around) Sqrt.FUNCTION.apply(Around.of(30, 40));
    Chop._12.requireClose(around.mean(), RealScalar.of(5.477225575051661));
    Chop._12.requireClose(around.uncertainty(), RealScalar.of(3.6514837167011076));
  }

  @Test
  void testAbsSquared() {
    Around around = (Around) AbsSquared.FUNCTION.apply(Around.of(ComplexScalar.of(2, 3), RealScalar.of(2)));
    ExactScalarQ.require(around.mean());
  }

  @Test
  void testExp() {
    Around around = (Around) Exp.FUNCTION.apply(Around.of(2, 3));
    Chop._12.requireClose(around.mean(), RealScalar.of(7.38905609893065));
    Chop._12.requireClose(around.uncertainty(), RealScalar.of(22.16716829679195));
  }

  @Test
  void testLog() {
    Around around = (Around) Log.FUNCTION.apply(Around.of(2, 3));
    Chop._12.requireClose(around.mean(), RealScalar.of(0.6931471805599453));
    assertEquals(around.uncertainty(), RationalScalar.of(3, 2));
  }

  @Test
  void testPower() {
    assertEquals(Power.of(Around.of(-4, 3), RealScalar.of(3)), Around.of(-64, 144));
    assertEquals(Power.of(Around.of(-3, 2), RealScalar.of(2)), Around.of(9, 12));
    assertEquals(Power.of( //
        Around.of(RationalScalar.HALF.negate(), RealScalar.TWO), RealScalar.of(-2)), //
        Around.of(4, 32));
    assertEquals(Power.of( //
        Around.of(RationalScalar.HALF.negate(), RealScalar.TWO), RealScalar.of(-3)), //
        Around.of(-8, 96));
  }

  @Test
  void testPowerFail() {
    assertThrows(TensorRuntimeException.class, () -> Power.of(Around.of(-3, 2), Around.of(9, 12)));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> Around.of(null, 2));
    assertThrows(NullPointerException.class, () -> Around.of(2, null));
    assertThrows(NullPointerException.class, () -> Around.of(Pi.VALUE, null));
    assertThrows(NullPointerException.class, () -> Around.of(null, Pi.VALUE));
  }
}
