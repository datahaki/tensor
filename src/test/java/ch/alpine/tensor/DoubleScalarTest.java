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

public class DoubleScalarTest {
  @Test
  public void testZero() {
    assertEquals(RealScalar.ZERO, DoubleScalar.of(0));
    assertFalse(DoubleScalar.of(0) instanceof RationalScalar);
  }

  @Test
  public void testAdd() {
    RealScalar.ZERO.hashCode();
    Tensor a = DoubleScalar.of(1.23);
    Tensor b = DoubleScalar.of(2.3);
    assertTrue(a.add(b).equals(b.add(a)));
    Tensor c = DoubleScalar.of(1.23 + 2.3);
    assertTrue(a.add(b).equals(c));
  }

  @Test
  public void testZeroReciprocal() {
    Scalar nzero = DoubleScalar.of(0.0);
    assertEquals(nzero.reciprocal(), DoubleScalar.POSITIVE_INFINITY);
    assertEquals(DoubleScalar.POSITIVE_INFINITY.reciprocal(), nzero);
  }

  @Test
  public void testChop() {
    Scalar s = DoubleScalar.of(3.14);
    assertEquals(Chop._12.of(s), s);
    Scalar r = DoubleScalar.of(1e-14);
    assertEquals(Chop._12.of(r), r.zero());
    assertEquals(Chop._12.of(RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  public void testEquality() {
    assertEquals(RealScalar.ONE, DoubleScalar.of(1));
    assertEquals(DoubleScalar.of(1), RationalScalar.of(1, 1));
    assertEquals(DoubleScalar.of(1), RealScalar.of(1));
  }

  @Test
  public void testInf() {
    Scalar inf = DoubleScalar.of(Double.POSITIVE_INFINITY);
    Scalar c = RealScalar.of(-2);
    assertEquals(inf.multiply(c), inf.negate());
    assertEquals(c.multiply(inf), inf.negate());
    Scalar nan = inf.multiply(inf.zero());
    assertTrue(Double.isNaN(nan.number().doubleValue()));
  }

  @Test
  public void testMin() {
    Scalar a = RealScalar.of(3);
    Scalar b = RealScalar.of(7.2);
    assertEquals(Min.of(a, b), a);
  }

  @Test
  public void testMax1() {
    Scalar a = RealScalar.of(3);
    Scalar b = RealScalar.of(7.2);
    assertEquals(Max.of(a, b), b);
  }

  @Test
  public void testMax2() {
    Scalar a = RealScalar.of(0);
    Scalar b = RealScalar.of(7.2);
    assertEquals(Max.of(a, b), b);
  }

  @Test
  public void testNegativeZero() {
    Scalar d1 = DoubleScalar.of(0.0);
    Scalar d2 = DoubleScalar.of(-0.0);
    assertEquals(d1.toString(), "0.0");
    assertEquals(d2.toString(), "-0.0"); // -0.0 is tolerated as value
    assertTrue(Scalars.isZero(d1));
    assertTrue(Scalars.isZero(d2));
    assertEquals(d1.subtract(d2).toString(), "0.0");
    assertEquals(d2.subtract(d1).toString(), "-0.0"); // -0.0 is tolerated as value
    assertTrue(Scalars.compare(d1, d2) == 0);
    assertTrue(d1.hashCode() == d2.hashCode());
    assertEquals(d1.hashCode(), d2.hashCode());
    assertEquals(d1.negate().toString(), "-0.0");
    assertEquals(d2.negate().toString(), "0.0");
  }

  @Test
  public void testNegZeroString() {
    Scalar scalar = Scalars.fromString("-0.0");
    assertInstanceOf(DoubleScalar.class, scalar);
    assertEquals(scalar.toString(), "0.0");
  }

  @Test
  public void testNegZeroSort() {
    Tensor vector = Tensors.vectorDouble(0.0, -0.0, -0.0, 0.0, -0.0, 0.0);
    Tensor sorted = Sort.of(vector);
    assertEquals(vector.toString(), sorted.toString());
  }

  @Test
  public void testDeleteDuplicates() {
    Tensor vector = DeleteDuplicates.of(Tensors.vectorDouble(0.0, -0.0, 0.0, -0.0));
    assertEquals(vector.length(), 1);
  }

  @Test
  public void testNaN() {
    DoubleScalar nan = (DoubleScalar) DoubleScalar.INDETERMINATE;
    assertThrows(TensorRuntimeException.class, () -> nan.isNonNegative());
    assertThrows(TensorRuntimeException.class, () -> nan.signum());
  }

  @Test
  public void testCompareFail() {
    Scalar a = RealScalar.of(7.2);
    Scalar b = GaussScalar.of(3, 5);
    assertThrows(TensorRuntimeException.class, () -> Max.of(a, b));
    assertThrows(TensorRuntimeException.class, () -> Max.of(b, a));
  }

  @Test
  public void testValue() {
    DoubleScalar doubleScalar = (DoubleScalar) DoubleScalar.of(3.14);
    assertEquals(doubleScalar.number(), 3.14);
  }

  @Test
  public void testEquals() {
    assertFalse(DoubleScalar.of(3.14).equals(null));
    assertFalse(DoubleScalar.of(3.14).equals(ComplexScalar.of(1, 2)));
  }

  @Test
  public void testObject() {
    Object object = DoubleScalar.of(3.14);
    assertFalse(object.equals("hello"));
  }

  @Test
  public void testSqrtNegZero() {
    Scalar scalar = DoubleScalar.of(-0.0);
    assertEquals(scalar.toString(), "-0.0");
    AbstractRealScalar ars = (AbstractRealScalar) scalar;
    boolean nonNegative = ars.isNonNegative();
    assertTrue(nonNegative);
    assertEquals(Sqrt.FUNCTION.apply(scalar), RealScalar.ZERO);
  }
}
