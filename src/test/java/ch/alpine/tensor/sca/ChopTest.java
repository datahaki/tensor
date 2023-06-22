// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

class ChopTest {
  @Test
  void testChop() {
    Tensor v = Tensors.vectorDouble(1e-10, 1e-12, 1e-14, 1e-16);
    Tensor c = v.map(Chop._12);
    assertNotEquals(c.get(0), RealScalar.ZERO);
    assertNotEquals(c.get(1), RealScalar.ZERO);
    assertEquals(c.get(2), RealScalar.ZERO);
    assertEquals(c.get(3), RealScalar.ZERO);
  }

  @Test
  void testCustom() {
    Chop chop = Chop.below(3.142);
    assertTrue(chop.isClose(DoubleScalar.of(Math.PI), RealScalar.ZERO));
    assertFalse(chop.isClose(DoubleScalar.of(3.15), RealScalar.ZERO));
  }

  @Test
  void testExclusive() {
    assertFalse(Chop._12.allZero(RealScalar.of(Chop._12.threshold())));
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(1e-9, "kg");
    Scalar act = Quantity.of(0, "kg");
    assertEquals(Chop._07.apply(qs1), act);
    assertEquals(Chop._10.apply(qs1), qs1);
  }

  @Test
  void testFail() {
    try {
      Chop.below(-1e-9);
      fail();
    } catch (Exception exception) {
      assertEquals(exception.getMessage(), "-1.0E-9");
    }
  }

  @Test
  void testRequireNonZero() {
    assertEquals(Chop._06.requireNonZero(Pi.TWO), Pi.TWO);
    assertThrows(Throw.class, () -> Chop._06.requireNonZero(RealScalar.of(1e-10)));
  }

  @Test
  void testComplex() {
    assertTrue(Chop._05.isClose( //
        ComplexScalar.of(1.2, 3.1), //
        ComplexScalar.of(1.2, 3.1000006)));
  }

  @Test
  void testNaN() {
    Scalar scalar = Chop._05.apply(DoubleScalar.INDETERMINATE);
    assertInstanceOf(DoubleScalar.class, scalar);
    assertTrue(Double.isNaN(scalar.number().doubleValue()));
  }

  @Test
  void testInf() {
    Scalar scalar = Chop._05.apply(DoubleScalar.NEGATIVE_INFINITY);
    assertInstanceOf(DoubleScalar.class, scalar);
    assertTrue(Double.isInfinite(scalar.number().doubleValue()));
  }

  @Test
  void testClose() {
    Scalar s1 = DoubleScalar.of(1);
    Scalar s2 = DoubleScalar.of(1 + 1e-10);
    assertTrue(Chop._07.isClose(s1, s2));
    assertTrue(Chop._09.isClose(s1, s2));
    assertFalse(Chop._10.isClose(s1, s2));
    assertFalse(Chop._12.isClose(s1, s2));
  }

  @Test
  void testCloseExact() {
    Scalar s1 = RationalScalar.of(1, 10000000);
    Scalar s2 = RationalScalar.of(2, 10000000);
    assertFalse(Chop._05.isClose(s1, s2));
    assertTrue(Chop._05.isClose(N.DOUBLE.apply(s1), N.DOUBLE.apply(s2)));
    Scalar s3 = RationalScalar.of(1, 10000000);
    assertTrue(Chop._05.isClose(s1, s3));
  }

  @Test
  void testDecimal() {
    Scalar scalar = DecimalScalar.of(new BigDecimal("0.0000001"));
    assertTrue(Chop._05.allZero(scalar));
    assertFalse(Chop._10.allZero(scalar));
  }

  @Test
  void testCloseNaNFail() {
    Chop.below(Double.POSITIVE_INFINITY);
    assertThrows(IllegalArgumentException.class, () -> Chop.below(Double.NaN));
  }

  @Test
  void testCloseFail() {
    assertThrows(IllegalArgumentException.class, () -> Chop._05.isClose(Tensors.vector(1), Tensors.vector(1, 1)));
  }

  @Test
  void testRequireCloseScalar() {
    Chop._06.requireClose(RealScalar.of(2), RealScalar.of(2.000000001));
    assertThrows(Throw.class, () -> Chop._06.requireClose(RealScalar.of(2), RealScalar.of(2.1)));
  }

  @Test
  void testRequireCloseTensor() {
    Chop._03.requireClose(Tensors.vector(1, 2, 3.00001), Tensors.vector(1, 2.00001, 3));
    assertThrows(Exception.class, () -> Chop._03.requireClose(Tensors.vector(1, 2, 3.00001), Tensors.vector(1, 2.01, 3)));
  }

  @Test
  void testRequireCloseFormatFail() {
    assertThrows(IllegalArgumentException.class, () -> Chop._03.requireClose(Tensors.vector(1, 2, 3), Tensors.vector(1, 2)));
  }

  @Test
  void testRequireCloseException() {
    Tensor b1 = Tensors.vector(2, 3);
    Tensor b2 = Tensors.vector(2, 4);
    try {
      Chop._40.requireClose(b1, b2);
    } catch (Exception exception) {
      assertEquals(exception.getMessage(), "Throw[3, 4, -1]");
    }
  }

  @Test
  void testRequireZero() {
    Chop._04.requireZero(RealScalar.of(1e-8));
    Chop._04.requireAllZero(RealScalar.of(1e-8));
    assertThrows(Throw.class, () -> Chop._04.requireZero(RealScalar.of(1e-2)));
    assertThrows(Throw.class, () -> Chop._04.requireAllZero(RealScalar.of(1e-2)));
  }

  @Test
  void testRequireAllZero() {
    Tensor tensor = Tensors.vector(0, 0, 0, 1e-5);
    Chop._04.requireAllZero(tensor);
    assertThrows(Throw.class, () -> Chop._06.requireAllZero(tensor));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(Tolerance.CHOP);
  }

  @Test
  void testToString() {
    assertTrue(Tolerance.CHOP.toString().startsWith("Chop["));
  }
}
