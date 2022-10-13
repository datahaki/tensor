// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;

class ComplexHelperTest {
  @Test
  void testAdd() {
    Scalar a = Scalars.fromString("-13*I[m]");
    Scalar b = Scalars.fromString("-3/7[m]");
    Scalar c = a.add(b);
    Scalar d = Scalars.fromString("-3/7-13*I[m]");
    assertEquals(c, d);
    assertInstanceOf(Quantity.class, c);
  }

  @Test
  void testPolar() {
    Scalar abs = Quantity.of(2, "V*m^-1");
    Scalar q = ComplexScalar.fromPolar(abs, RealScalar.ONE);
    assertInstanceOf(Quantity.class, q);
    Scalar modulus = Abs.of(q);
    assertEquals(modulus, abs);
  }

  @Test
  void testUnder1() {
    Scalar c = ComplexScalar.of(2, 3);
    Scalar q = Quantity.of(1, "V");
    Scalar cuq = c.under(q);
    assertInstanceOf(Quantity.class, cuq);
    Scalar qdc = q.divide(c);
    assertInstanceOf(Quantity.class, qdc);
    Scalar crq = c.reciprocal().multiply(q);
    assertInstanceOf(Quantity.class, crq);
    assertEquals(cuq, crq);
    assertEquals(cuq, qdc);
  }

  @Test
  void testUnder2() {
    Scalar c = ComplexScalar.of(2, 3);
    Scalar q = Quantity.of(1, "V");
    Scalar quc = q.under(c);
    assertInstanceOf(Quantity.class, quc);
    Scalar cdq = c.divide(q);
    assertInstanceOf(Quantity.class, cdq);
    Scalar qrc = q.reciprocal().multiply(c);
    assertInstanceOf(Quantity.class, qrc);
    assertEquals(quc, qrc);
    assertEquals(quc, cdq);
  }

  @Test
  void testUnder3() {
    Scalar q1 = Quantity.of(ComplexScalar.of(2, 3), "m");
    Scalar q2 = Quantity.of(ComplexScalar.of(-1, 7), "V");
    Scalar quc = q1.under(q2);
    assertInstanceOf(Quantity.class, quc);
    Scalar cdq = q2.divide(q1);
    assertInstanceOf(Quantity.class, cdq);
    Scalar qrc = q1.reciprocal().multiply(q2);
    assertInstanceOf(Quantity.class, qrc);
    assertEquals(quc, qrc);
    assertEquals(quc, cdq);
  }

  @Test
  void testUnder4() {
    Scalar q1 = Quantity.of(ComplexScalar.of(2, 3), "m");
    Scalar q2 = Quantity.of(ComplexScalar.of(-1, 7), "m");
    Scalar quc = q1.under(q2);
    assertInstanceOf(ComplexScalar.class, quc);
    Scalar cdq = q2.divide(q1);
    assertInstanceOf(ComplexScalar.class, cdq);
    Scalar qrc = q1.reciprocal().multiply(q2);
    assertInstanceOf(ComplexScalar.class, qrc);
    assertEquals(quc, qrc);
    assertEquals(quc, cdq);
  }

  @Test
  void testPlusQuantity() {
    Scalar c = ComplexScalar.of(2, 3);
    Scalar q = Quantity.of(0, "V");
    // Mathematica 12 does not resolve this
    assertThrows(Throw.class, () -> c.add(q));
  }

  @Test
  void testSqrt() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 100; ++count) {
      Scalar re = RandomVariate.of(distribution);
      Scalar im = RandomVariate.of(distribution);
      Scalar scalar = ComplexScalar.of(re, im);
      Scalar ref = ComplexHelper.sqrtPolar(scalar);
      Scalar cmp = ComplexHelper.sqrt(re, im);
      Tolerance.CHOP.requireClose(ref, cmp);
    }
  }

  @Test
  void testEpsilonP1() {
    Scalar re = RealScalar.of(4.9E-324);
    Scalar im = RealScalar.of(4.9E-324);
    Scalar scalar = ComplexScalar.of(re, im);
    Scalar ref = ComplexHelper.sqrtPolar(scalar);
    Scalar cmp = ComplexHelper.sqrt(re, im);
    Scalar mathematica = ComplexScalar.of(2.432040959320809E-162, 1.007384349597552E-162);
    boolean lessThan = Scalars.lessThan( //
        Abs.between(cmp, mathematica), //
        Abs.between(ref, mathematica));
    assertTrue(lessThan);
  }

  @Test
  void testEpsilonN1() {
    Scalar re = RealScalar.of(-4.9E-324);
    Scalar im = RealScalar.of(4.9E-324);
    Scalar scalar = ComplexScalar.of(re, im);
    Scalar ref = ComplexHelper.sqrtPolar(scalar);
    Scalar cmp = ComplexHelper.sqrt(re, im);
    Scalar mathematica = ComplexScalar.of(1.007384349597552E-162, 2.432040959320809E-162);
    boolean lessThan = Scalars.lessThan( //
        Abs.between(cmp, mathematica), //
        Abs.between(ref, mathematica));
    assertTrue(lessThan);
  }

  @Test
  void testEpsMultiplicationIssue() {
    // Mathematica:
    // x = 1.5717277847026288*^-162 + 1.5717277847026285*^-162 I
    // x x = 5.*10^-324 + 0. I
    // Warning: too small to represent as a normalized machine number; precision may be lost.
    Scalar re = RealScalar.of(0);
    Scalar im = RealScalar.of(4.9E-324);
    Scalar x = ComplexHelper.sqrt(re, im);
    Scalar res = x.multiply(x); // gives 4.9E-324 as in mathematica
    assertFalse(Scalars.isZero(res));
  }

  @Test
  void testPlusQuantityFail() {
    Scalar c = ComplexScalar.of(2, 3);
    Scalar q = Quantity.of(1, "V");
    assertThrows(Throw.class, () -> c.add(q));
  }

  @Test
  void testQuantityFail() {
    Scalar c = Quantity.of(3, "m");
    Scalar r = Pi.VALUE;
    assertThrows(Throw.class, () -> ComplexScalar.of(c, r));
    assertThrows(Throw.class, () -> ComplexScalar.of(r, c));
  }
}
