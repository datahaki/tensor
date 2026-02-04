package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ComplexEmbedding;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.N;

class QuexTest {
  @Test
  void testBasics() {
    Scalar a = Quex.of(4, 5, 2);
    Scalar b = Quex.of(7, 3, 2);
    Scalar c = a.multiply(b);
    assertInstanceOf(Quex.class, c);
    Tolerance.CHOP.requireClose(N.DOUBLE.apply(a).multiply(b), N.DOUBLE.apply(c));
    Scalar d = a.add(b);
    assertInstanceOf(Quex.class, d);
    Tolerance.CHOP.requireClose(N.DOUBLE.apply(a).add(b), N.DOUBLE.apply(d));
    Scalar e = a.divide(b);
    assertInstanceOf(Quex.class, e);
    Tolerance.CHOP.requireClose(N.DOUBLE.apply(a).divide(b), N.DOUBLE.apply(e));
    Scalar f = a.subtract(b);
    assertInstanceOf(Quex.class, f);
    Tolerance.CHOP.requireClose(N.DOUBLE.apply(a).subtract(b), N.DOUBLE.apply(f));
  }

  @Test
  void testComplexEmbedding() {
    assertInstanceOf(ComplexEmbedding.class, Quex.of(4, 5, 2));
  }

  @Test
  void testSpecialAdd() {
    assertInstanceOf(RationalScalar.class, Quex.of(2, 0, 2));
    Scalar a = Quex.of(4, 5, 2);
    assertInstanceOf(Quex.class, a);
    Scalar b = Quex.of(7, 5, 2);
    Scalar c = b.subtract(a);
    assertInstanceOf(RationalScalar.class, c);
    assertEquals(c, RealScalar.of(3));
    Scalar d = a.add(RealScalar.of(10));
    assertEquals(d, Quex.of(14, 5, 2));
    assertEquals(a.multiply(RealScalar.of(10)), Quex.of(40, 50, 2));
    assertEquals(a.multiply(RealScalar.of(1.)), N.DOUBLE.apply(a));
  }

  @Test
  void testSpecialBlub() {
    Scalar a = Quex.of(4, 5, 2);
    assertEquals(a, a);
    Scalar b = Quex.of(7, 5, 3);
    assertNotEquals(a, b);
    Scalar c = b.subtract(a);
    assertInstanceOf(DoubleScalar.class, c);
    Set<Object> set = new HashSet<>();
    set.add(a);
    set.add(b);
    set.add(Quex.of(4, 5, 2));
    assertEquals(set.size(), 2);
    assertEquals(a.toString(), "(4+5*Sqrt[2])");
  }

  @Test
  void testSpecialMul() {
    Scalar a = Quex.of(2, 4, 2);
    Scalar b = Quex.of(-2, 4, 2);
    Scalar c = a.multiply(b);
    assertInstanceOf(RationalScalar.class, c);
    assertEquals(c, RealScalar.of(28));
    Tolerance.CHOP.requireClose(N.DOUBLE.apply(a).multiply(b), N.DOUBLE.apply(c));
  }

  @Test
  void testReciprocal() {
    Scalar a = Quex.of(2, 3, 13);
    Scalar b = Quex.of(-2, 7, 13);
    assertEquals(a.divide(b), b.under(a));
    assertEquals(a.reciprocal().multiply(a), a.one());
  }

  @Test
  void testMulZero() {
    Scalar a = Quex.of(2, 3, 13);
    Scalar z = a.multiply(RealScalar.ZERO);
    assertInstanceOf(RationalScalar.class, z);
    assertEquals(z, RealScalar.ZERO);
  }

  @Test
  void testZeroCases() {
    assertInstanceOf(RationalScalar.class, Quex.of(4, 0, 2));
    assertInstanceOf(RationalScalar.class, Quex.of(4, 2, 0));
  }
}
