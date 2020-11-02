// code by jph
package ch.ethz.idsc.tensor;

import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ComplexHelperTest extends TestCase {
  public void testAdd() {
    Scalar a = Scalars.fromString("-13*I[m]");
    Scalar b = Scalars.fromString("-3/7[m]");
    Scalar c = a.add(b);
    Scalar d = Scalars.fromString("-3/7-13*I[m]");
    assertEquals(c, d);
    assertTrue(c instanceof Quantity);
  }

  public void testPolar() {
    Scalar abs = Quantity.of(2, "V*m^-1");
    Scalar q = ComplexScalar.fromPolar(abs, RealScalar.ONE);
    assertTrue(q instanceof Quantity);
    Scalar modulus = Abs.of(q);
    assertEquals(modulus, abs);
  }

  public void testUnder1() {
    Scalar c = ComplexScalar.of(2, 3);
    Scalar q = Quantity.of(1, "V");
    Scalar cuq = c.under(q);
    assertTrue(cuq instanceof Quantity);
    Scalar qdc = q.divide(c);
    assertTrue(qdc instanceof Quantity);
    Scalar crq = c.reciprocal().multiply(q);
    assertTrue(crq instanceof Quantity);
    assertEquals(cuq, crq);
    assertEquals(cuq, qdc);
  }

  public void testUnder2() {
    Scalar c = ComplexScalar.of(2, 3);
    Scalar q = Quantity.of(1, "V");
    Scalar quc = q.under(c);
    assertTrue(quc instanceof Quantity);
    Scalar cdq = c.divide(q);
    assertTrue(cdq instanceof Quantity);
    Scalar qrc = q.reciprocal().multiply(c);
    assertTrue(qrc instanceof Quantity);
    assertEquals(quc, qrc);
    assertEquals(quc, cdq);
  }

  public void testUnder3() {
    Scalar q1 = Quantity.of(ComplexScalar.of(2, 3), "m");
    Scalar q2 = Quantity.of(ComplexScalar.of(-1, 7), "V");
    Scalar quc = q1.under(q2);
    assertTrue(quc instanceof Quantity);
    Scalar cdq = q2.divide(q1);
    assertTrue(cdq instanceof Quantity);
    Scalar qrc = q1.reciprocal().multiply(q2);
    assertTrue(qrc instanceof Quantity);
    assertEquals(quc, qrc);
    assertEquals(quc, cdq);
  }

  public void testUnder4() {
    Scalar q1 = Quantity.of(ComplexScalar.of(2, 3), "m");
    Scalar q2 = Quantity.of(ComplexScalar.of(-1, 7), "m");
    Scalar quc = q1.under(q2);
    assertTrue(quc instanceof ComplexScalar);
    Scalar cdq = q2.divide(q1);
    assertTrue(cdq instanceof ComplexScalar);
    Scalar qrc = q1.reciprocal().multiply(q2);
    assertTrue(qrc instanceof ComplexScalar);
    assertEquals(quc, qrc);
    assertEquals(quc, cdq);
  }

  public void testPlusQuantity() {
    Scalar c = ComplexScalar.of(2, 3);
    Scalar q = Quantity.of(0, "V");
    Scalar p = c.add(q);
    assertTrue(p instanceof ComplexScalar);
  }

  public void testSqrt() {
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

  public void testEpsilonP1() {
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

  public void testEpsilonN1() {
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

  public void testEpsilon01() {
    Scalar re = RealScalar.of(0);
    Scalar im = RealScalar.of(4.9E-324);
    Scalar scalar = ComplexScalar.of(re, im);
    Scalar ref = ComplexHelper.sqrtPolar(scalar);
    Scalar cmp = ComplexHelper.sqrt(re, im);
    // TODO result not satisfactory!
    System.out.println(ref);
    System.out.println(cmp);
    System.out.println(ref.multiply(ref));
    System.out.println(cmp.multiply(cmp));
    // 2.432040959320809*10^-162 + 1.007384349597552*10^-162 I
  }

  public void testPlusQuantityFail() {
    Scalar c = DeterminateScalarQ.require(ComplexScalar.of(2, 3));
    Scalar q = DeterminateScalarQ.require(Quantity.of(1, "V"));
    AssertFail.of(() -> c.add(q));
  }

  public void testQuantityFail() {
    Scalar c = DeterminateScalarQ.require(Quantity.of(3, "m"));
    Scalar r = DeterminateScalarQ.require(Pi.VALUE);
    AssertFail.of(() -> ComplexScalar.of(c, r));
    AssertFail.of(() -> ComplexScalar.of(r, c));
  }
}
