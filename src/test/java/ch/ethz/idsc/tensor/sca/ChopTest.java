// code by jph
package ch.ethz.idsc.tensor.sca;

import java.math.BigDecimal;

import ch.ethz.idsc.tensor.DecimalScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ChopTest extends TestCase {
  public void testChop() {
    Tensor v = Tensors.vectorDouble(1e-10, 1e-12, 1e-14, 1e-16);
    Tensor c = v.map(Chop._12);
    assertFalse(c.get(0).equals(RealScalar.ZERO));
    assertFalse(c.get(1).equals(RealScalar.ZERO));
    assertTrue(c.get(2).equals(RealScalar.ZERO));
    assertTrue(c.get(3).equals(RealScalar.ZERO));
  }

  public void testCustom() {
    Chop chop = Chop.below(3.142);
    assertTrue(chop.isClose(DoubleScalar.of(Math.PI), RealScalar.ZERO));
    assertFalse(chop.isClose(DoubleScalar.of(3.15), RealScalar.ZERO));
  }

  public void testExclusive() {
    assertFalse(Chop._12.allZero(RealScalar.of(Chop._12.threshold())));
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(1e-9, "kg");
    Scalar act = Quantity.of(0, "kg");
    assertEquals(Chop._07.of(qs1), act);
    assertEquals(Chop._10.of(qs1), qs1);
  }

  public void testFail() {
    try {
      Chop.below(-1e-9);
      fail();
    } catch (Exception exception) {
      assertEquals(exception.getMessage(), "-1.0E-9");
    }
  }

  public void testRequireNonZero() {
    assertEquals(Chop._06.requireNonZero(Pi.TWO), Pi.TWO);
    AssertFail.of(() -> Chop._06.requireNonZero(RealScalar.of(1e-10)));
  }

  public void testComplex() {
    assertTrue(Chop._05.isClose( //
        Scalars.fromString("1.2+3.1*I"), //
        Scalars.fromString("1.2+3.1000006*I")));
  }

  public void testNaN() {
    Scalar scalar = Chop._05.apply(DoubleScalar.INDETERMINATE);
    assertTrue(scalar instanceof DoubleScalar);
    assertTrue(Double.isNaN(scalar.number().doubleValue()));
  }

  public void testInf() {
    Scalar scalar = Chop._05.apply(DoubleScalar.NEGATIVE_INFINITY);
    assertTrue(scalar instanceof DoubleScalar);
    assertTrue(Double.isInfinite(scalar.number().doubleValue()));
  }

  public void testClose() {
    Scalar s1 = DoubleScalar.of(1);
    Scalar s2 = DoubleScalar.of(1 + 1e-10);
    assertTrue(Chop._07.isClose(s1, s2));
    assertTrue(Chop._09.isClose(s1, s2));
    assertFalse(Chop._10.isClose(s1, s2));
    assertFalse(Chop._12.isClose(s1, s2));
  }

  public void testCloseExact() {
    Scalar s1 = RationalScalar.of(1, 10000000);
    Scalar s2 = RationalScalar.of(2, 10000000);
    assertFalse(Chop._05.isClose(s1, s2));
    assertTrue(Chop._05.isClose(N.DOUBLE.apply(s1), N.DOUBLE.apply(s2)));
    Scalar s3 = RationalScalar.of(1, 10000000);
    assertTrue(Chop._05.isClose(s1, s3));
  }

  public void testDecimal() {
    Scalar scalar = DecimalScalar.of(new BigDecimal("0.0000001"));
    assertTrue(Chop._05.allZero(scalar));
    assertFalse(Chop._10.allZero(scalar));
  }

  public void testCloseNaNFail() {
    Chop.below(Double.POSITIVE_INFINITY);
    AssertFail.of(() -> Chop.below(Double.NaN));
  }

  public void testCloseFail() {
    AssertFail.of(() -> Chop._05.isClose(Tensors.vector(1), Tensors.vector(1, 1)));
  }

  public void testRequireCloseScalar() {
    Chop._06.requireClose(RealScalar.of(2), RealScalar.of(2.000000001));
    AssertFail.of(() -> Chop._06.requireClose(RealScalar.of(2), RealScalar.of(2.1)));
  }

  public void testRequireCloseTensor() {
    Chop._03.requireClose(Tensors.vector(1, 2, 3.00001), Tensors.vector(1, 2.00001, 3));
    try {
      Chop._03.requireClose(Tensors.vector(1, 2, 3.00001), Tensors.vector(1, 2.01, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testRequireZero() {
    Chop._04.requireZero(RealScalar.of(1e-8));
    Chop._04.requireAllZero(RealScalar.of(1e-8));
    AssertFail.of(() -> Chop._04.requireZero(RealScalar.of(1e-2)));
    AssertFail.of(() -> Chop._04.requireAllZero(RealScalar.of(1e-2)));
  }

  public void testRequireAllZero() {
    Tensor tensor = Tensors.vector(0, 0, 0, 1e-5);
    Chop._04.requireAllZero(tensor);
    AssertFail.of(() -> Chop._06.requireAllZero(tensor));
  }

  public void testToString() {
    assertTrue(Tolerance.CHOP.toString().startsWith("Chop["));
  }
}
