// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.DeleteDuplicates;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Sqrt;

class DoubleScalarTest {
  @Test
  void testZero() {
    assertEquals(RealScalar.ZERO, DoubleScalar.of(0));
    assertFalse(DoubleScalar.of(0) instanceof RationalScalar);
  }

  @Test
  void testAdd() {
    RealScalar.ZERO.hashCode();
    Tensor a = DoubleScalar.of(1.23);
    Tensor b = DoubleScalar.of(2.3);
    assertEquals(a.add(b), b.add(a));
    Tensor c = DoubleScalar.of(1.23 + 2.3);
    assertEquals(a.add(b), c);
  }

  @Test
  void testZeroReciprocal() {
    Scalar nzero = DoubleScalar.of(0.0);
    assertEquals(nzero.reciprocal(), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(DoubleScalar.POSITIVE_INFINITY.reciprocal(), nzero);
  }

  @Test
  void testChop() {
    Scalar s = DoubleScalar.of(3.14);
    assertEquals(Chop._12.of(s), s);
    Scalar r = DoubleScalar.of(1e-14);
    assertEquals(Chop._12.of(r), r.zero());
    assertEquals(Chop._12.of(RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  void testEquality() {
    assertEquals(RealScalar.ONE, DoubleScalar.of(1));
    assertEquals(DoubleScalar.of(1), RationalScalar.of(1, 1));
    assertEquals(DoubleScalar.of(1), RealScalar.of(1));
  }

  @Test
  void testInf() {
    Scalar inf = DoubleScalar.of(Double.POSITIVE_INFINITY);
    Scalar c = RealScalar.of(-2);
    assertEquals(inf.multiply(c), inf.negate());
    assertEquals(c.multiply(inf), inf.negate());
    Scalar nan = inf.multiply(inf.zero());
    assertTrue(Double.isNaN(nan.number().doubleValue()));
  }

  @Test
  void testMin() {
    Scalar a = RealScalar.of(3);
    Scalar b = RealScalar.of(7.2);
    assertEquals(Min.of(a, b), a);
  }

  @Test
  void testMax1() {
    Scalar a = RealScalar.of(3);
    Scalar b = RealScalar.of(7.2);
    assertEquals(Max.of(a, b), b);
  }

  @Test
  void testMax2() {
    Scalar a = RealScalar.of(0);
    Scalar b = RealScalar.of(7.2);
    assertEquals(Max.of(a, b), b);
  }

  @Test
  void testNegativeZero() {
    Scalar d1 = DoubleScalar.of(0.0);
    Scalar d2 = DoubleScalar.of(-0.0);
    assertEquals(d1.toString(), "0.0");
    assertEquals(d2.toString(), "-0.0"); // -0.0 is tolerated as value
    assertTrue(Scalars.isZero(d1));
    assertTrue(Scalars.isZero(d2));
    assertEquals(d1.subtract(d2).toString(), "0.0");
    assertEquals(d2.subtract(d1).toString(), "-0.0"); // -0.0 is tolerated as value
    assertEquals(Scalars.compare(d1, d2), 0);
    assertEquals(d1.hashCode(), d2.hashCode());
    assertEquals(d1.negate().toString(), "-0.0");
    assertEquals(d2.negate().toString(), "0.0");
  }

  @Test
  void testNegZeroString() {
    Scalar scalar = Scalars.fromString("-0.0");
    assertInstanceOf(DoubleScalar.class, scalar);
    assertEquals(scalar.toString(), "0.0");
  }

  @Test
  void testNegZeroSort() {
    Tensor vector = Tensors.vectorDouble(0.0, -0.0, -0.0, 0.0, -0.0, 0.0);
    Tensor sorted = Sort.of(vector);
    assertEquals(vector.toString(), sorted.toString());
  }

  @Test
  void testDeleteDuplicates() {
    Tensor vector = DeleteDuplicates.of(Tensors.vectorDouble(0.0, -0.0, 0.0, -0.0));
    assertEquals(vector.length(), 1);
  }

  @Test
  void testNaN() {
    DoubleScalar nan = (DoubleScalar) DoubleScalar.INDETERMINATE;
    assertThrows(Throw.class, nan::isNonNegative);
    assertThrows(Throw.class, nan::signum);
  }

  @Test
  void testCompareFail() {
    Scalar a = RealScalar.of(7.2);
    Scalar b = GaussScalar.of(3, 5);
    assertThrows(Throw.class, () -> Max.of(a, b));
    assertThrows(Throw.class, () -> Max.of(b, a));
  }

  @Test
  void testValue() {
    DoubleScalar doubleScalar = (DoubleScalar) DoubleScalar.of(3.14);
    assertEquals(doubleScalar.number(), 3.14);
  }

  @Test
  void testEquals() {
    assertFalse(DoubleScalar.of(3.14).equals(null));
    assertFalse(DoubleScalar.of(3.14).equals(ComplexScalar.of(1, 2)));
  }

  @Test
  void testObject() {
    Object object = DoubleScalar.of(3.14);
    assertFalse(object.equals("hello"));
  }

  @Test
  void testSqrtNegZero() {
    Scalar scalar = DoubleScalar.of(-0.0);
    assertEquals(scalar.toString(), "-0.0");
    AbstractRealScalar ars = (AbstractRealScalar) scalar;
    boolean nonNegative = ars.isNonNegative();
    assertTrue(nonNegative);
    assertEquals(Sqrt.FUNCTION.apply(scalar), RealScalar.ZERO);
  }
}
