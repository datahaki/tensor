// code by jph
package ch.alpine.tensor.qty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;

public class QuantityMultiplicativeTest {
  @Test
  public void testMultiplyScalar() {
    Scalar qs1 = Quantity.of(3, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Scalar qs3 = Quantity.of(5, "m");
    Tensor vec = Tensors.of(qs1, qs2, qs3);
    Tensor sca = vec.multiply(RealScalar.of(3));
    assertEquals(sca.toString(), "{9[m], 12[m], 15[m]}");
  }

  @Test
  public void testMultiply() {
    Scalar qs1 = Quantity.of(3, "m");
    Scalar qs2 = Quantity.of(-2, "s");
    assertEquals(qs1.multiply(qs2).toString(), "-6[m*s]");
  }

  @Test
  public void testDivide() {
    Scalar qs1 = Quantity.of(12, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Scalar qs3 = Quantity.of(3, "m^0");
    assertEquals(qs1.divide(qs2), qs3);
    assertTrue(qs3 instanceof RationalScalar);
  }

  @Test
  public void testReciprocal() {
    Scalar qs1 = Quantity.of(4, "m");
    assertEquals(qs1.reciprocal().toString(), "1/4[m^-1]");
  }

  private static void _checkDivision(Scalar q1, Scalar q2) {
    assertEquals(q1.divide(q2), q2.under(q1));
    assertEquals(q2.divide(q1), q1.under(q2));
  }

  @Test
  public void testDivisionUnder() {
    _checkDivision(Quantity.of(1, "m"), Quantity.of(2, "s"));
    _checkDivision(Quantity.of(1, "m"), DoubleScalar.of(2.0));
    _checkDivision(Quantity.of(1, "m"), RealScalar.of(2));
    double eps = Math.nextUp(0.0);
    _checkDivision(Quantity.of(eps, "m"), Quantity.of(2, "s"));
    _checkDivision(Quantity.of(eps, "m"), DoubleScalar.of(2.0));
    _checkDivision(Quantity.of(eps, "m"), RealScalar.of(2));
    // ---
    _checkDivision(Quantity.of(1, "m"), Quantity.of(eps, "s"));
    _checkDivision(Quantity.of(1, "m"), DoubleScalar.of(eps));
    _checkDivision(Quantity.of(1, "m"), RealScalar.of(eps));
    // ---
    _checkDivision(Quantity.of(0, "m"), Quantity.of(eps, "s"));
    _checkDivision(Quantity.of(0, "m"), DoubleScalar.of(eps));
    _checkDivision(Quantity.of(0.0, "m"), Quantity.of(eps, "s"));
    _checkDivision(Quantity.of(0.0, "m"), DoubleScalar.of(eps));
    // ---
    _checkDivision(Quantity.of(eps, "m"), Quantity.of(eps, "s"));
    _checkDivision(Quantity.of(eps, "m"), DoubleScalar.of(eps));
  }

  @Test
  public void testDivision1() {
    Scalar quantity = Quantity.of(0, "m");
    Scalar eps = DoubleScalar.of(Math.nextUp(0.0));
    assertTrue(Scalars.isZero(quantity.divide(eps)));
  }

  @Test
  public void testDivision2() {
    Scalar zero = DoubleScalar.of(0.0);
    Scalar eps = Quantity.of(Math.nextUp(0.0), "m");
    assertTrue(Scalars.isZero(zero.divide(eps)));
  }

  @Test
  public void testDivision3() {
    Scalar s1 = ComplexScalar.of(1, 2);
    Scalar s2 = Quantity.of(3, "m");
    assertEquals(s1.divide(s2), s2.under(s1));
    assertEquals(s2.divide(s1), s1.under(s2));
    ExactScalarQ.require(s1.divide(s2));
    ExactScalarQ.require(s2.divide(s1));
    ExactScalarQ.require(s1.under(s2));
    ExactScalarQ.require(s2.under(s1));
  }
}
