// code by jph
package ch.alpine.tensor.sca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DecimalScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

public class ChopTest {
  @Test
  public void testChop() {
    Tensor v = Tensors.vectorDouble(1e-10, 1e-12, 1e-14, 1e-16);
    Tensor c = v.map(Chop._12);
    assertFalse(c.get(0).equals(RealScalar.ZERO));
    assertFalse(c.get(1).equals(RealScalar.ZERO));
    assertTrue(c.get(2).equals(RealScalar.ZERO));
    assertTrue(c.get(3).equals(RealScalar.ZERO));
  }

  @Test
  public void testCustom() {
    Chop chop = Chop.below(3.142);
    assertTrue(chop.isClose(DoubleScalar.of(Math.PI), RealScalar.ZERO));
    assertFalse(chop.isClose(DoubleScalar.of(3.15), RealScalar.ZERO));
  }

  @Test
  public void testExclusive() {
    assertFalse(Chop._12.allZero(RealScalar.of(Chop._12.threshold())));
  }

  @Test
  public void testQuantity() {
    Scalar qs1 = Quantity.of(1e-9, "kg");
    Scalar act = Quantity.of(0, "kg");
    assertEquals(Chop._07.of(qs1), act);
    assertEquals(Chop._10.of(qs1), qs1);
  }

  @Test
  public void testFail() {
    try {
      Chop.below(-1e-9);
      fail();
    } catch (Exception exception) {
      assertEquals(exception.getMessage(), "-1.0E-9");
    }
  }

  @Test
  public void testRequireNonZero() {
    assertEquals(Chop._06.requireNonZero(Pi.TWO), Pi.TWO);
    assertThrows(TensorRuntimeException.class, () -> Chop._06.requireNonZero(RealScalar.of(1e-10)));
  }

  @Test
  public void testComplex() {
    assertTrue(Chop._05.isClose( //
        Scalars.fromString("1.2+3.1*I"), //
        Scalars.fromString("1.2+3.1000006*I")));
  }

  @Test
  public void testNaN() {
    Scalar scalar = Chop._05.apply(DoubleScalar.INDETERMINATE);
    assertTrue(scalar instanceof DoubleScalar);
    assertTrue(Double.isNaN(scalar.number().doubleValue()));
  }

  @Test
  public void testInf() {
    Scalar scalar = Chop._05.apply(DoubleScalar.NEGATIVE_INFINITY);
    assertTrue(scalar instanceof DoubleScalar);
    assertTrue(Double.isInfinite(scalar.number().doubleValue()));
  }

  @Test
  public void testClose() {
    Scalar s1 = DoubleScalar.of(1);
    Scalar s2 = DoubleScalar.of(1 + 1e-10);
    assertTrue(Chop._07.isClose(s1, s2));
    assertTrue(Chop._09.isClose(s1, s2));
    assertFalse(Chop._10.isClose(s1, s2));
    assertFalse(Chop._12.isClose(s1, s2));
  }

  @Test
  public void testCloseExact() {
    Scalar s1 = RationalScalar.of(1, 10000000);
    Scalar s2 = RationalScalar.of(2, 10000000);
    assertFalse(Chop._05.isClose(s1, s2));
    assertTrue(Chop._05.isClose(N.DOUBLE.apply(s1), N.DOUBLE.apply(s2)));
    Scalar s3 = RationalScalar.of(1, 10000000);
    assertTrue(Chop._05.isClose(s1, s3));
  }

  @Test
  public void testDecimal() {
    Scalar scalar = DecimalScalar.of(new BigDecimal("0.0000001"));
    assertTrue(Chop._05.allZero(scalar));
    assertFalse(Chop._10.allZero(scalar));
  }

  @Test
  public void testCloseNaNFail() {
    Chop.below(Double.POSITIVE_INFINITY);
    assertThrows(IllegalArgumentException.class, () -> Chop.below(Double.NaN));
  }

  @Test
  public void testCloseFail() {
    assertThrows(IllegalArgumentException.class, () -> Chop._05.isClose(Tensors.vector(1), Tensors.vector(1, 1)));
  }

  @Test
  public void testRequireCloseScalar() {
    Chop._06.requireClose(RealScalar.of(2), RealScalar.of(2.000000001));
    assertThrows(TensorRuntimeException.class, () -> Chop._06.requireClose(RealScalar.of(2), RealScalar.of(2.1)));
  }

  @Test
  public void testRequireCloseTensor() {
    Chop._03.requireClose(Tensors.vector(1, 2, 3.00001), Tensors.vector(1, 2.00001, 3));
    try {
      Chop._03.requireClose(Tensors.vector(1, 2, 3.00001), Tensors.vector(1, 2.01, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  @Test
  public void testRequireCloseFormatFail() {
    assertThrows(IllegalArgumentException.class, () -> Chop._03.requireClose(Tensors.vector(1, 2, 3), Tensors.vector(1, 2)));
  }

  @Test
  public void testRequireCloseException() {
    Tensor b1 = Tensors.vector(2, 3);
    Tensor b2 = Tensors.vector(2, 4);
    try {
      Chop._40.requireClose(b1, b2);
    } catch (Exception exception) {
      assertEquals(exception.getMessage(), "3; 4; -1");
    }
  }

  @Test
  public void testRequireZero() {
    Chop._04.requireZero(RealScalar.of(1e-8));
    Chop._04.requireAllZero(RealScalar.of(1e-8));
    assertThrows(TensorRuntimeException.class, () -> Chop._04.requireZero(RealScalar.of(1e-2)));
    assertThrows(TensorRuntimeException.class, () -> Chop._04.requireAllZero(RealScalar.of(1e-2)));
  }

  @Test
  public void testRequireAllZero() {
    Tensor tensor = Tensors.vector(0, 0, 0, 1e-5);
    Chop._04.requireAllZero(tensor);
    assertThrows(TensorRuntimeException.class, () -> Chop._06.requireAllZero(tensor));
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(Tolerance.CHOP);
  }

  @Test
  public void testToString() {
    assertTrue(Tolerance.CHOP.toString().startsWith("Chop["));
  }
}
