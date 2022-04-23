// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.jet.JetScalar;
import ch.alpine.tensor.num.Binomial;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

public class FiniteQTest {
  @Test
  public void testRealFinite() {
    assertTrue(FiniteQ.of(RealScalar.of(0.)));
    assertTrue(FiniteQ.of(RealScalar.ZERO));
  }

  @Test
  public void testComplex() {
    assertTrue(FiniteQ.of(ComplexScalar.of(0., 0.3)));
    assertTrue(FiniteQ.of(ComplexScalar.of(0., 2)));
  }

  @Test
  public void testComplexCorner() {
    assertFalse(FiniteQ.of(ComplexScalar.of(Double.POSITIVE_INFINITY, 0.3)));
    assertFalse(FiniteQ.of(ComplexScalar.of(0., Double.NaN)));
  }

  @Test
  public void testGauss() {
    assertTrue(FiniteQ.of(GaussScalar.of(3, 7)));
    assertTrue(FiniteQ.of(GaussScalar.of(0, 7)));
  }

  @Test
  public void testCorner() {
    assertFalse(FiniteQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(FiniteQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(FiniteQ.of(RealScalar.of(Double.NaN)));
  }

  @Test
  public void testQuantity() {
    assertTrue(FiniteQ.of(Quantity.of(3, "m")));
    assertTrue(FiniteQ.of(Quantity.of(3.1415, "m")));
  }

  @Test
  public void testAll() {
    assertTrue(FiniteQ.all(Tensors.vector(1, 1, 1.)));
    assertTrue(FiniteQ.all(Tensors.vector(1, 2, 3)));
  }

  @Test
  public void testTrue() {
    assertTrue(FiniteQ.of(Pi.HALF));
    assertTrue(FiniteQ.of(RationalScalar.HALF));
    assertTrue(FiniteQ.of(Scalars.fromString("2+3*I")));
    assertTrue(FiniteQ.of(Scalars.fromString("2+3.4*I")));
    assertTrue(FiniteQ.of(Scalars.fromString("2+3.4*I[s^3]")));
    assertTrue(FiniteQ.of(Scalars.fromString("8.2+3.3*I[m^2]")));
    assertTrue(FiniteQ.of(Quantity.of(Pi.VALUE, "kg")));
    assertTrue(FiniteQ.of(Scalars.fromString("abc")));
  }

  @Test
  public void testFalse() {
    assertFalse(FiniteQ.of(DoubleScalar.POSITIVE_INFINITY));
    assertFalse(FiniteQ.of(DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(FiniteQ.of(DoubleScalar.INDETERMINATE));
    assertFalse(FiniteQ.of(Scalars.fromString("8.2+NaN*I[m^2]")));
    assertFalse(FiniteQ.of(Scalars.fromString("NaN+2*I[m*s]")));
    assertFalse(FiniteQ.of(Scalars.fromString("NaN+NaN*I[m*s]")));
    assertFalse(FiniteQ.of(Quantity.of(DoubleScalar.POSITIVE_INFINITY, "s")));
    assertFalse(FiniteQ.of(Quantity.of(DoubleScalar.NEGATIVE_INFINITY, "N")));
    assertFalse(FiniteQ.of(Quantity.of(DoubleScalar.INDETERMINATE, "m")));
    assertFalse(FiniteQ.of(Quantity.of(ComplexScalar.of(3, Double.NaN), "m")));
    assertFalse(FiniteQ.of(Quantity.of(ComplexScalar.of(Double.NaN, 3), "m")));
    assertFalse(FiniteQ.of(Binomial.of(RealScalar.of(123412341234324L), RealScalar.ZERO)));
  }

  @Test
  public void testDecimalScalar() {
    assertTrue(FiniteQ.of(DecimalScalar.of(BigDecimal.ONE)));
    assertTrue(FiniteQ.of(DecimalScalar.of(new BigDecimal("0.001"))));
  }

  @Test
  public void testJetScalar() {
    assertTrue(FiniteQ.of(JetScalar.of(Pi.VALUE, 3)));
  }

  @Test
  public void testComplexBranching() {
    Scalar scalar = ComplexScalar.of(Double.NaN, Double.NaN);
    assertTrue(scalar instanceof ComplexScalar);
    assertFalse(FiniteQ.of(scalar));
    assertTrue(FiniteQ.of(ComplexScalar.of(1, 2)));
    assertFalse(FiniteQ.of(ComplexScalar.of(3, Double.NaN)));
    assertFalse(FiniteQ.of(ComplexScalar.of(Double.NaN, 4)));
    assertFalse(FiniteQ.of(ComplexScalar.of(Double.NaN, Double.NaN)));
  }

  @Test
  public void testInvariance() {
    Scalar scalar = Scalars.fromString("NaN+2*I[m*s]");
    assertEquals(scalar.toString(), "NaN+2*I[m*s]");
    assertTrue(scalar instanceof Quantity);
    assertThrows(TensorRuntimeException.class, () -> FiniteQ.require(scalar));
  }

  @Test
  public void testRequireThrow() {
    FiniteQ.require(Pi.VALUE);
    assertThrows(TensorRuntimeException.class, () -> FiniteQ.require(DoubleScalar.POSITIVE_INFINITY));
  }

  @Test
  public void testNullFail() {
    assertThrows(NullPointerException.class, () -> FiniteQ.of(null));
  }
}
