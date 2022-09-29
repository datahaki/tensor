// code by jph
package ch.alpine.tensor.pdf.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.PDF;
import ch.alpine.tensor.pdf.TestMarkovChebyshev;
import ch.alpine.tensor.qty.DateTime;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.qty.UnitConvert;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Variance;
import ch.alpine.tensor.sca.Sign;

class StudentTDistributionTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = Serialization.copy(StudentTDistribution.of(2, 3, 5));
    PDF pdf = PDF.of(distribution);
    Tolerance.CHOP.requireClose( //
        pdf.at(RealScalar.of(1.75)), //
        RealScalar.of(0.1260097929094335));
    assertEquals(distribution.toString(), "StudentTDistribution[2, 3, 5]");
  }

  @Test
  void testMeanVar() {
    Distribution distribution = StudentTDistribution.of(5, 4, 3);
    assertEquals(ExactScalarQ.require(Mean.of(distribution)), RealScalar.of(5));
    assertEquals(ExactScalarQ.require(Variance.of(distribution)), RealScalar.of(48));
  }

  @Test
  void testMeanVarSpecial() {
    assertFalse(FiniteScalarQ.of(Mean.of(StudentTDistribution.of(5, 4, 0.5))));
    assertFalse(FiniteScalarQ.of(Variance.of(StudentTDistribution.of(5, 4, 1.5))));
  }

  @Test
  void testDateTime() {
    DateTime mu = DateTime.of(2020, 12, 20, 4, 30);
    Distribution distribution = StudentTDistribution.of(mu, Quantity.of(100_000, "s"), RealScalar.of(2));
    PDF pdf = PDF.of(distribution);
    Scalar x = DateTime.of(2020, 12, 20, 4, 33);
    Scalar scalar = pdf.at(x);
    Sign.requirePositive(scalar);
    Unit unit = QuantityUnit.of(scalar);
    assertEquals(unit, Unit.of("s^-1"));
  }

  @Test
  void testDateTimeHour() {
    DateTime mu = DateTime.of(2020, 12, 20, 4, 30);
    Distribution distribution = StudentTDistribution.of(mu, Quantity.of(10, "h"), RealScalar.of(2.3));
    PDF pdf = PDF.of(distribution);
    Scalar x = DateTime.of(2020, 12, 20, 4, 33);
    Scalar p = pdf.at(x);
    Sign.requirePositive(p);
    assertEquals(QuantityUnit.of(p), Unit.of("s^-1"));
    ScalarUnaryOperator suo = UnitConvert.SI().to("h^2");
    Tolerance.CHOP.requireClose(suo.apply(Variance.of(distribution)), Quantity.of(766.6666666666666, "h^2"));
  }

  @Test
  void testMonotonous() {
    TestMarkovChebyshev.monotonous(StudentTDistribution.of(5, 4, 3));
  }

  @Test
  void testComplexFail() {
    assertThrows(ClassCastException.class, () -> StudentTDistribution.of(ComplexScalar.of(1, 2), RealScalar.ONE, RealScalar.ONE));
  }

  @Test
  void testQuantityFail() {
    assertThrows(Throw.class, () -> StudentTDistribution.of(Quantity.of(3, "m"), Quantity.of(2, "km"), RealScalar.ONE));
    assertThrows(Throw.class, () -> StudentTDistribution.of(Quantity.of(0, "s"), Quantity.of(2, "m"), RealScalar.ONE));
    assertThrows(Throw.class, () -> StudentTDistribution.of(Quantity.of(0, ""), Quantity.of(2, "m"), RealScalar.ONE));
  }

  @Test
  void testNegativeSigmaFail() {
    NormalDistribution.of(5, 1);
    assertThrows(Throw.class, () -> StudentTDistribution.of(5, -1, 1));
  }
}
