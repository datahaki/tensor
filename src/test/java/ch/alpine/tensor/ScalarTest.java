// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.num.GaussScalar;

class ScalarTest {
  @Test
  void testIsScalar() {
    assertInstanceOf(Scalar.class, DoubleScalar.POSITIVE_INFINITY);
  }

  @Test
  void testLengthNegative() {
    assertTrue(Scalar.LENGTH < 0);
  }

  @Test
  void testGet() {
    Tensor t = RealScalar.of(3);
    Scalar s = (Scalar) t;
    assertEquals(t, s);
  }

  @Test
  void testUnmodifiable() {
    Scalar s = RealScalar.of(3);
    assertEquals(s.unmodifiable(), s);
  }

  @Test
  void testNumber() {
    Scalar zero = RealScalar.ZERO;
    assertEquals(zero.number().getClass(), Integer.class);
    long asd = (Integer) zero.number();
    assertEquals(Double.valueOf(-1.9).intValue(), -1 + asd);
    assertEquals(Double.valueOf(1.9).intValue(), 1);
  }

  @Test
  void testDimensions() {
    Scalar a = DoubleScalar.of(3);
    assertEquals(Dimensions.of(a), Collections.emptyList());
    Scalar b = GaussScalar.of(3, 7);
    assertEquals(Dimensions.of(b), Collections.emptyList());
  }

  @Test
  void testFails() {
    Scalar a = DoubleScalar.of(3);
    Scalar b = DoubleScalar.of(5);
    assertThrows(Throw.class, () -> a.dot(b));
  }

  @Test
  void testNumber2() {
    Scalar a = DoubleScalar.of(3);
    Scalar b = DoubleScalar.of(5);
    Number na = a.number();
    Number nb = b.number();
    Scalar c = a.add(b);
    Scalar d = DoubleScalar.of(na.doubleValue() + nb.doubleValue());
    assertEquals(c, d);
  }

  @Test
  void testEquals() {
    assertNotEquals(null, Tensors.empty());
    assertNotEquals(null, RealScalar.ZERO);
    assertNotEquals(null, DoubleScalar.of(0.3));
    assertNotEquals(null, RationalScalar.of(5, 3));
    assertNotEquals(null, ComplexScalar.of(RationalScalar.of(5, 3), RationalScalar.of(5, 3)));
    assertNotEquals(null, Integer.valueOf(1233));
  }

  @Test
  void testIteratorFail() {
    assertThrows(Exception.class, () -> {
      for (Tensor entry : RealScalar.ZERO) {
        entry.copy();
      }
    });
  }
}
