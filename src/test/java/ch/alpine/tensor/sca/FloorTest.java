// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;

public class FloorTest {
  @Test
  public void testFloor() {
    assertEquals(Floor.of(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(Floor.of(RationalScalar.of(-5, 2)), RationalScalar.of(-3, 1));
    assertEquals(Floor.of(RationalScalar.of(5, 2)), RationalScalar.of(2, 1));
    assertEquals(Floor.of(DoubleScalar.of(0.123)), RealScalar.ZERO);
    assertEquals(Floor.of(RealScalar.ONE), RealScalar.ONE);
    assertEquals(Floor.of(DoubleScalar.of(-0.123)), RationalScalar.of(-1, 1));
  }

  @Test
  public void testHash() {
    Tensor a = Tensors.of( //
        DoubleScalar.of(0.123), DoubleScalar.of(3.343), DoubleScalar.of(-0.123));
    Tensor b = a.map(Floor.FUNCTION);
    Tensor c = a.map(Floor.FUNCTION);
    assertEquals(b, c);
    assertEquals(b.hashCode(), c.hashCode());
  }

  @Test
  public void testGetFloor() {
    Tensor v = Tensors.vectorDouble(3.5, 5.6, 9.12);
    Scalar s = Floor.of(v.Get(1));
    RealScalar rs = (RealScalar) s;
    assertEquals(rs.number().doubleValue(), 5.0);
  }

  @Test
  public void testLarge() {
    Scalar scalar = DoubleScalar.of(1e30);
    Scalar r = Round.of(scalar);
    Scalar f = Floor.of(scalar);
    assertEquals(r, f);
    assertEquals(r.toString(), "1000000000000000000000000000000");
  }

  @Test
  public void testRational1() {
    Scalar s = RationalScalar.of(234534584545L, 13423656767L); // 17.4717
    assertEquals(Floor.intValueExact(s), 17);
    assertEquals(Floor.longValueExact(s), 17);
    Scalar r = Floor.of(s);
    assertEquals(r, RealScalar.of(17));
    assertInstanceOf(RationalScalar.class, r);
  }

  @Test
  public void testIntExactValueFail() {
    assertThrows(TensorRuntimeException.class, () -> Floor.intValueExact(Quantity.of(1.2, "h")));
    assertThrows(TensorRuntimeException.class, () -> Floor.longValueExact(Quantity.of(4.5, "km*h^-1")));
  }

  @Test
  public void testRational2() {
    Scalar s = RationalScalar.of(734534584545L, 13423656767L); // 54.7194
    Scalar r = Floor.of(s);
    assertEquals(r, RealScalar.of(54));
    assertInstanceOf(RationalScalar.class, r);
  }

  @Test
  public void testComplex() {
    Scalar c = Scalars.fromString("7-2*I");
    assertEquals(Floor.of(c), c);
    Scalar d = Scalars.fromString("7.9-1.1*I");
    assertEquals(Floor.of(d), c);
  }

  @Test
  public void testQuantity() {
    Scalar scalar = Quantity.of(210.9, "K");
    assertEquals(Floor.FUNCTION.apply(scalar), Quantity.of(210, "K"));
  }

  @Test
  public void testMultiple() {
    Scalar w = Quantity.of(2, "K");
    ScalarUnaryOperator suo = Floor.toMultipleOf(w);
    assertEquals(suo.apply(Quantity.of(3.9, "K")), w);
    assertEquals(suo.apply(Quantity.of(-2, "K")), w.negate());
    assertEquals(suo.apply(Quantity.of(-2.1, "K")), w.multiply(RealScalar.of(-2)));
    assertEquals(suo.apply(Quantity.of(-3.9, "K")), w.multiply(RealScalar.of(-2)));
  }

  @Test
  public void testPositiveInfinity() {
    Scalar scalar = DoubleScalar.POSITIVE_INFINITY;
    assertEquals(Floor.FUNCTION.apply(scalar), scalar);
  }

  @Test
  public void testNegativeInfinity() {
    Scalar scalar = DoubleScalar.NEGATIVE_INFINITY;
    assertEquals(Floor.FUNCTION.apply(scalar), scalar);
  }

  @Test
  public void testNaN() {
    assertTrue(Double.isNaN(Floor.FUNCTION.apply(DoubleScalar.INDETERMINATE).number().doubleValue()));
    assertTrue(Double.isNaN(Floor._2.apply(DoubleScalar.INDETERMINATE).number().doubleValue()));
  }

  @Test
  public void testDecimalPlacesP() {
    Scalar w = Quantity.of(1.99999, "K");
    Tolerance.CHOP.requireClose(Floor._1.apply(w), Quantity.of(1.9, "K"));
    Tolerance.CHOP.requireClose(Floor._2.apply(w), Quantity.of(1.99, "K"));
    Tolerance.CHOP.requireClose(Floor._3.apply(w), Quantity.of(1.999, "K"));
  }

  @Test
  public void testDecimalPlacesN() {
    Scalar w = Quantity.of(-1.00000001, "K");
    Tolerance.CHOP.requireClose(Floor._1.apply(w), Quantity.of(-1.1, "K"));
    Tolerance.CHOP.requireClose(Floor._2.apply(w), Quantity.of(-1.01, "K"));
    Tolerance.CHOP.requireClose(Floor._3.apply(w), Quantity.of(-1.001, "K"));
  }

  @Test
  public void testTypeFail() {
    assertThrows(TensorRuntimeException.class, () -> Floor.of(StringScalar.of("some")));
  }
}
