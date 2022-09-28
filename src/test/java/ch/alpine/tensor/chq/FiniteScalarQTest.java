// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.jet.DateTime;
import ch.alpine.tensor.jet.JetScalar;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Round;

class FiniteScalarQTest {
  @Test
  void testRealFinite() {
    assertTrue(FiniteScalarQ.of(RealScalar.of(0.)));
    assertTrue(FiniteScalarQ.of(RealScalar.ZERO));
  }

  @Test
  void testComplex() {
    assertTrue(FiniteScalarQ.of(ComplexScalar.of(0., 0.3)));
    assertTrue(FiniteScalarQ.of(ComplexScalar.of(0., 2)));
  }

  @Test
  void testComplexCorner() {
    assertFalse(FiniteScalarQ.of(ComplexScalar.of(Double.POSITIVE_INFINITY, 0.3)));
    assertFalse(FiniteScalarQ.of(ComplexScalar.of(0., Double.NaN)));
  }

  @Test
  void testGauss() {
    assertTrue(FiniteScalarQ.of(GaussScalar.of(3, 7)));
    assertTrue(FiniteScalarQ.of(GaussScalar.of(0, 7)));
  }

  @Test
  void testCorner() {
    assertFalse(FiniteScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(FiniteScalarQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(FiniteScalarQ.of(RealScalar.of(Double.NaN)));
  }

  @Test
  void testQuantity() {
    assertTrue(FiniteScalarQ.of(Quantity.of(3, "m")));
    assertTrue(FiniteScalarQ.of(Quantity.of(3.1415, "m")));
  }

  @Test
  void testTrue() {
    assertTrue(FiniteScalarQ.of(Pi.HALF));
    assertTrue(FiniteScalarQ.of(RationalScalar.HALF));
    assertTrue(FiniteScalarQ.of(Scalars.fromString("2+3*I")));
    assertTrue(FiniteScalarQ.of(Scalars.fromString("2+3.4*I")));
    assertTrue(FiniteScalarQ.of(Scalars.fromString("2+3.4*I[s^3]")));
    assertTrue(FiniteScalarQ.of(Scalars.fromString("8.2+3.3*I[m^2]")));
    assertTrue(FiniteScalarQ.of(Quantity.of(Pi.VALUE, "kg")));
    assertTrue(FiniteScalarQ.of(Scalars.fromString("abc")));
  }

  @Test
  void testFalse() {
    assertFalse(FiniteScalarQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(FiniteScalarQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(FiniteScalarQ.of(DoubleScalar.INDETERMINATE));
    assertFalse(FiniteScalarQ.of(Scalars.fromString("8.2+NaN*I[m^2]")));
    assertFalse(FiniteScalarQ.of(Scalars.fromString("NaN+2*I[m*s]")));
    assertFalse(FiniteScalarQ.of(Scalars.fromString("NaN+NaN*I[m*s]")));
    assertFalse(FiniteScalarQ.of(Quantity.of(DoubleScalar.POSITIVE_INFINITY, "s")));
    assertFalse(FiniteScalarQ.of(Quantity.of(DoubleScalar.NEGATIVE_INFINITY, "N")));
    assertFalse(FiniteScalarQ.of(Quantity.of(DoubleScalar.INDETERMINATE, "m")));
    assertFalse(FiniteScalarQ.of(Quantity.of(ComplexScalar.of(3, Double.NaN), "m")));
    assertFalse(FiniteScalarQ.of(Quantity.of(ComplexScalar.of(Double.NaN, 3), "m")));
    assertFalse(FiniteScalarQ.of(Binomial.of(RealScalar.of(123412341234324L), RealScalar.ZERO)));
  }

  @Test
  void testDecimalScalar() {
    assertTrue(FiniteScalarQ.of(DecimalScalar.of(BigDecimal.ONE)));
    assertTrue(FiniteScalarQ.of(DecimalScalar.of(new BigDecimal("0.001"))));
  }

  @Test
  void testJetScalar() {
    assertTrue(FiniteScalarQ.of(JetScalar.of(Pi.VALUE, 3)));
  }

  @Test
  void testRoundFalse() {
    assertFalse(FiniteScalarQ.of(Round._2.apply(DoubleScalar.INDETERMINATE)));
    assertFalse(FiniteScalarQ.of(Round._2.apply(DoubleScalar.POSITIVE_INFINITY)));
  }

  void testRoundTrue() {
    assertTrue(FiniteScalarQ.of(Round._2.apply(Pi.VALUE)));
  }

  @Test
  void testComplexBranching() {
    Scalar scalar = ComplexScalar.of(Double.NaN, Double.NaN);
    assertInstanceOf(ComplexScalar.class, scalar);
    assertFalse(FiniteScalarQ.of(scalar));
    assertTrue(FiniteScalarQ.of(ComplexScalar.of(1, 2)));
    assertFalse(FiniteScalarQ.of(ComplexScalar.of(3, Double.NaN)));
    assertFalse(FiniteScalarQ.of(ComplexScalar.of(Double.NaN, 4)));
    assertFalse(FiniteScalarQ.of(ComplexScalar.of(Double.NaN, Double.NaN)));
  }

  @Test
  void testInvariance() {
    Scalar scalar = Scalars.fromString("NaN+2*I[m*s]");
    assertEquals(scalar.toString(), "NaN+2*I[m*s]");
    assertInstanceOf(Quantity.class, scalar);
    assertThrows(Throw.class, () -> FiniteScalarQ.require(scalar));
  }

  @Test
  void testDateTime() {
    assertTrue(ExactScalarQ.of(DateTime.now()));
    assertTrue(FiniteScalarQ.of(DateTime.now()));
  }

  @Test
  void testRequireThrow() {
    FiniteScalarQ.require(Pi.VALUE);
    assertThrows(Throw.class, () -> FiniteScalarQ.require(DoubleScalar.POSITIVE_INFINITY));
  }

  private static boolean isNaN(Scalar scalar) {
    return !scalar.equals(scalar);
  }

  @Test
  void testIsNaN() {
    assertTrue(isNaN(ComplexScalar.of(Double.NaN, Double.NaN)));
    assertTrue(isNaN(RealScalar.of(Double.NaN)));
    assertTrue(isNaN(Quantity.of(Double.NaN, "s")));
    assertFalse(isNaN(RealScalar.of(Double.POSITIVE_INFINITY)));
  }

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> FiniteScalarQ.of(null));
  }
}
