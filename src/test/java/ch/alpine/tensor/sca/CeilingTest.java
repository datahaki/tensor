// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.StringScalar;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;

class CeilingTest {
  @Test
  void testCeiling() {
    assertEquals(Ceiling.FUNCTION.apply(RealScalar.ZERO), RealScalar.ZERO);
    assertEquals(Ceiling.FUNCTION.apply(Rational.of(-5, 2)), Rational.of(-2, 1));
    assertEquals(Ceiling.FUNCTION.apply(Rational.of(5, 2)), Rational.of(3, 1));
    assertEquals(Ceiling.FUNCTION.apply(DoubleScalar.of(0.123)), RealScalar.ONE);
    assertEquals(Ceiling.FUNCTION.apply(RealScalar.ONE), RealScalar.ONE);
    assertEquals(Ceiling.FUNCTION.apply(DoubleScalar.of(-0.123)), Rational.of(0, 1));
  }

  @Test
  void testHash() {
    Tensor a = Tensors.of( //
        DoubleScalar.of(0.123), DoubleScalar.of(3.343), DoubleScalar.of(-0.123));
    Tensor b = a.maps(Ceiling.FUNCTION);
    Tensor c = a.maps(Ceiling.FUNCTION);
    assertEquals(b, c);
    assertEquals(b.hashCode(), c.hashCode());
  }

  @Test
  void testGetCeiling() {
    Tensor v = Tensors.vectorDouble(3.5, 5.6, 9.12);
    Scalar s = Ceiling.FUNCTION.apply(v.Get(1));
    RealScalar rs = (RealScalar) s;
    assertEquals(rs.number(), 6);
  }

  @Test
  void testComplex() {
    Scalar c = ComplexScalar.of(7, -2);
    assertEquals(Ceiling.FUNCTION.apply(c), c);
    Scalar d = ComplexScalar.of(6.1, -2.1);
    assertEquals(Ceiling.FUNCTION.apply(d), c);
  }

  @Test
  void testRational1() {
    Scalar s = Rational.of(234534584545L, 13423656767L); // 17.4717
    assertEquals(Ceiling.intValueExact(s), 18);
    assertEquals(Ceiling.longValueExact(s), 18);
    Scalar r = Ceiling.FUNCTION.apply(s);
    assertEquals(r, RealScalar.of(18));
    assertInstanceOf(Rational.class, r);
  }

  @Test
  void testIntExactValueFail() {
    assertThrows(Throw.class, () -> Ceiling.intValueExact(Quantity.of(1.2, "h")));
    assertThrows(Throw.class, () -> Ceiling.longValueExact(Quantity.of(2.3, "h*s")));
  }

  @Test
  void testRational2() {
    Scalar s = Rational.of(734534584545L, 13423656767L); // 54.7194
    Scalar r = Ceiling.FUNCTION.apply(s);
    assertEquals(r, RealScalar.of(55));
    assertInstanceOf(Rational.class, r);
  }

  @Test
  void testLarge() {
    BigInteger bi = new BigInteger("97826349587623498756234545976");
    Scalar s = RealScalar.of(bi);
    Scalar r = Ceiling.FUNCTION.apply(s);
    assertEquals(s, r);
    assertInstanceOf(Rational.class, r);
  }

  @Test
  void testMultiple() {
    Scalar w = Quantity.of(2, "K");
    ScalarUnaryOperator suo = Ceiling.toMultipleOf(w);
    assertEquals(suo.apply(Quantity.of(3.9, "K")), w.multiply(RealScalar.of(2)));
    assertEquals(suo.apply(Quantity.of(-2, "K")), w.negate());
    assertEquals(suo.apply(Quantity.of(-2.1, "K")), w.multiply(RealScalar.of(-1)));
    assertEquals(suo.apply(Quantity.of(-3.9, "K")), w.multiply(RealScalar.of(-1)));
  }

  @Test
  void testDecimalPlacesP() {
    Scalar w = Quantity.of(1.00001, "K");
    Tolerance.CHOP.requireClose(Ceiling._1.apply(w), Quantity.of(1.1, "K"));
    Tolerance.CHOP.requireClose(Ceiling._2.apply(w), Quantity.of(1.01, "K"));
    Tolerance.CHOP.requireClose(Ceiling._3.apply(w), Quantity.of(1.001, "K"));
  }

  @Test
  void testDecimalPlacesN() {
    Scalar w = Quantity.of(-1.99999, "K");
    Tolerance.CHOP.requireClose(Ceiling._1.apply(w), Quantity.of(-1.9, "K"));
    Tolerance.CHOP.requireClose(Ceiling._2.apply(w), Quantity.of(-1.99, "K"));
    Tolerance.CHOP.requireClose(Ceiling._3.apply(w), Quantity.of(-1.999, "K"));
  }

  @Test
  void testPositiveInfinity() {
    Scalar scalar = DoubleScalar.POSITIVE_INFINITY;
    assertEquals(Ceiling.FUNCTION.apply(scalar), scalar);
  }

  @Test
  void testNegativeInfinity() {
    Scalar scalar = DoubleScalar.NEGATIVE_INFINITY;
    assertEquals(Ceiling.FUNCTION.apply(scalar), scalar);
  }

  @Test
  void testNaN() {
    assertTrue(Double.isNaN(Ceiling.FUNCTION.apply(DoubleScalar.INDETERMINATE).number().doubleValue()));
    assertTrue(Double.isNaN(Ceiling._2.apply(DoubleScalar.INDETERMINATE).number().doubleValue()));
  }

  @Test
  void testQuantity() {
    Scalar scalar = Quantity.of(2.1, "K");
    assertEquals(Ceiling.FUNCTION.apply(scalar), Quantity.of(3, "K"));
  }

  @Test
  void testTypeFail() {
    Scalar scalar = StringScalar.of("string");
    assertThrows(Throw.class, () -> Ceiling.FUNCTION.apply(scalar));
  }
}
