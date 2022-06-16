// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;

class AbsTest {
  @Test
  void testReal() {
    assertEquals(Abs.FUNCTION.apply(RealScalar.of(+3)), RealScalar.of(3));
    assertEquals(Abs.FUNCTION.apply(RealScalar.of(-3)), RealScalar.of(3));
  }

  @Test
  void testComplex() {
    assertEquals(Abs.FUNCTION.apply(ComplexScalar.of(3, 4)), RealScalar.of(5));
    assertEquals(Abs.FUNCTION.apply(ComplexScalar.of(4, 3)), RealScalar.of(5));
  }

  @Test
  void testBetween() {
    Scalar a = Quantity.of(+9, "s");
    Scalar b = Quantity.of(+5, "s");
    Scalar c = Quantity.of(-2, "s");
    assertEquals(Abs.between(a, b), Quantity.of(4, "s"));
    assertEquals(Abs.between(b, a), Quantity.of(4, "s"));
    assertEquals(Abs.between(a, c), Quantity.of(11, "s"));
    assertEquals(Abs.between(c, a), Quantity.of(11, "s"));
    assertEquals(Abs.between(b, c), Quantity.of(7, "s"));
    assertEquals(Abs.between(c, b), Quantity.of(7, "s"));
  }

  @Test
  void testNaN() {
    assertEquals(Abs.FUNCTION.apply(DoubleScalar.INDETERMINATE).toString(), "NaN");
  }
}
