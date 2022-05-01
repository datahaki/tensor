// code by jph
package ch.alpine.tensor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.num.GaussScalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Real;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;

class ComplexScalarImplTest {
  @Test
  public void testAbs() {
    ComplexScalar s = (ComplexScalar) ComplexScalar.of(RationalScalar.of(-2, 3), RationalScalar.of(-5, 100));
    // ----------------------------------------- 0.668539037337719303091638399542
    Scalar a = Abs.of(s); // ------------------- 0.6685390373377194
    Scalar c = RationalScalar.of(1609, 3600); // 0.6685390373377192
    Tensor r = Sqrt.of(c);
    double d = Math.sqrt(c.number().doubleValue());
    Tolerance.CHOP.requireClose(a, r);
    String prefix = "0.668539037337719";
    assertTrue(a.toString().startsWith(prefix));
    assertTrue(r.toString().startsWith(prefix));
    assertTrue((d + "").startsWith(prefix));
  }

  @Test
  public void testAbs2() {
    Scalar s = ComplexScalar.of(RealScalar.of(-3), RealScalar.of(4));
    assertInstanceOf(RationalScalar.class, Abs.of(s));
  }

  @Test
  public void testFalseConstruct() {
    Scalar c1 = ComplexScalar.of(3, -4);
    Scalar c2 = ComplexScalar.of(-2, 9);
    assertThrows(TensorRuntimeException.class, () -> ComplexScalar.of(c1, c2));
    Scalar r1 = RealScalar.of(7);
    assertThrows(TensorRuntimeException.class, () -> ComplexScalar.of(r1, c2));
    assertThrows(TensorRuntimeException.class, () -> ComplexScalar.of(c1, r1));
  }

  @Test
  public void testMultiply() {
    Scalar c = ComplexScalar.of(RealScalar.of(2), RationalScalar.of(5, 8));
    assertEquals(Real.of(c), RealScalar.of(2));
    assertEquals(Imag.of(c), RationalScalar.of(5, 8));
    assertEquals(Abs.of(c), Vector2Norm.of(Tensors.of(RealScalar.of(2), RationalScalar.of(5, 8))));
    Scalar r = RealScalar.of(-6);
    assertEquals(Real.of(r), r);
    assertEquals(Imag.of(r), RealScalar.ZERO);
    Scalar r2 = RealScalar.of(6);
    assertEquals(r.multiply(c).toString(), "-12-15/4*I");
    assertEquals(c.multiply(r).toString(), "-12-15/4*I");
    assertEquals(r2.multiply(c).toString(), "12+15/4*I");
    assertEquals(c.multiply(r2).toString(), "12+15/4*I");
  }

  @Test
  public void testSolveCR() {
    int n = 8;
    Random random = new Random();
    Tensor A = Tensors.matrix((i, j) -> //
    ComplexScalar.of( //
        RealScalar.of(random.nextInt(35)), //
        RealScalar.of(random.nextInt(35))), n, n);
    Tensor b = Tensors.matrix((i, j) -> ComplexScalar.of(//
        random.nextInt(15), //
        random.nextInt(15)), n, n + 3);
    Tensor X = LinearSolve.of(A, b);
    Tensor err = A.dot(X).subtract(b);
    assertEquals(A.dot(X), b);
    List<Integer> list = Dimensions.of(b);
    assertEquals(err, ConstantArray.of(RealScalar.ZERO, list));
    assertEquals(err, Array.zeros(list));
    Chop.NONE.requireAllZero(err);
  }

  @Test
  public void testParsing() {
    Scalar c1 = ComplexScalar.of(RealScalar.of(2), RationalScalar.of(5, 8));
    Scalar c2 = ComplexScalar.of(RealScalar.ZERO, RationalScalar.of(5, 8));
    Scalar c3 = ComplexScalar.of(RealScalar.of(2), RationalScalar.of(-5, 8));
    Scalar c4 = ComplexScalar.of(RealScalar.ZERO, RationalScalar.of(-5, 8));
    assertEquals("2+5/8*I", c1.toString());
    assertEquals("5/8*I", c2.toString());
    assertEquals("2-5/8*I", c3.toString());
    assertEquals("-5/8*I", c4.toString());
    assertEquals(c1, Scalars.fromString(c1.toString()));
    assertEquals(c2, Scalars.fromString(c2.toString()));
    assertEquals(c3, Scalars.fromString(c3.toString()));
    assertEquals(c4, Scalars.fromString(c4.toString()));
    assertEquals(c2, Scalars.fromString("5/8*I"));
    assertEquals(c4, Scalars.fromString("-5/8*I"));
  }

  @Test
  public void testEquals() {
    Scalar c1 = ComplexScalar.of(RealScalar.of(2), RationalScalar.of(5, 8));
    Scalar c2 = ComplexScalar.of(RealScalar.of(2), RationalScalar.of(5, 8));
    Scalar s1 = RationalScalar.of(3, 2);
    assertFalse(c1.equals(s1));
    assertFalse(s1.equals(c1));
    assertEquals(c1.hashCode(), c2.hashCode());
  }

  @Test
  public void testSerializable() throws Exception {
    Scalar a = ComplexScalar.of(3, 5.2345);
    assertEquals(a, Serialization.parse(Serialization.of(a)));
    assertEquals(a, Serialization.copy(a));
  }

  @Test
  public void testConjugate() {
    Scalar s = ComplexScalar.of(RationalScalar.of(-2, 3), RationalScalar.of(-5, 100));
    assertEquals(AbsSquared.of(s), RationalScalar.of(1609, 3600));
    ComplexScalar c = (ComplexScalar) ComplexScalar.of(RealScalar.of(2), RationalScalar.of(5, 8));
    Scalar ra = RationalScalar.of(5, 8);
    assertEquals(Conjugate.of(ra), RationalScalar.of(5, 8));
    Scalar s1 = Conjugate.of(c);
    Scalar s2 = ComplexScalar.of(RealScalar.of(2), ra.negate());
    assertEquals(s1.toString(), s2.toString());
    assertEquals(s1, s2);
    assertEquals(s2, s1);
    assertEquals(c.imag(), ra);
  }

  @Test
  public void testPower() {
    assertEquals(Power.of(ComplexScalar.I, 3), ComplexScalar.I.negate());
  }

  @Test
  public void testFloor() {
    Scalar s = ComplexScalar.of(121.3, -111.1);
    Scalar r = ComplexScalar.of(121, -112);
    assertEquals(Floor.of(s), r);
  }

  @Test
  public void testRound() {
    Scalar s = ComplexScalar.of(12.3, -3.9);
    Scalar r = ComplexScalar.of(12, -4);
    assertEquals(Round.of(s), r);
  }

  @Test
  public void testPower2() {
    Scalar s = ComplexScalar.of(RationalScalar.of(2, 7), RationalScalar.of(-4, 3));
    assertEquals(Power.of(s, -3), Scalars.fromString("-16086357/68921000-10955763/34460500*I"));
  }

  @Test
  public void testToString() {
    Scalar c2 = ComplexScalar.of(RealScalar.ZERO, RationalScalar.of(5, 8));
    assertEquals(c2.toString(), "5/8*I");
  }

  @Test
  public void testTensor() {
    Tensor u = Tensors.fromString("{I, 3/4-5*I}");
    Tensor uc = Conjugate.of(u);
    assertEquals(uc.toString(), "{-I, 3/4+5*I}");
  }

  private static void _assertDivideSymmetric(Scalar s1, Scalar s2) {
    // boolean equals = s1.divide(s2).equals(s2.under(s1));
    assertEquals(s1.divide(s2).toString(), s2.under(s1).toString());
  }

  @Test
  public void testDivision1() {
    Scalar zero = RealScalar.ZERO;
    Scalar eps = ComplexScalar.of(0, 5.562684646268010E-309);
    assertEquals(zero.divide(eps), RealScalar.ZERO);
    _assertDivideSymmetric(zero, eps);
  }

  @Test
  public void testDivision2a() {
    double eps = 5.562684646268010E-309;
    Scalar reps = RealScalar.of(eps);
    Scalar ieps = ComplexScalar.of(0, eps);
    assertEquals(reps.divide(ieps), ComplexScalar.of(0, -1));
    assertEquals(ieps.divide(reps), ComplexScalar.of(0, 1));
    _assertDivideSymmetric(reps, ieps);
    _assertDivideSymmetric(ieps, reps);
  }

  @Test
  public void testDivision3() {
    Scalar reps = ComplexScalar.of(0, Double.MIN_VALUE);
    assertEquals(reps.divide(reps), RealScalar.ONE);
  }

  @Test
  public void testDivision4() {
    final double eps = Double.MIN_VALUE;
    {
      Scalar reps = ComplexScalar.of(eps, eps);
      Scalar ieps = ComplexScalar.of(0, eps);
      assertEquals(reps.divide(ieps), ComplexScalar.of(1, -1));
      _assertDivideSymmetric(reps, ieps);
    }
    {
      Scalar reps = ComplexScalar.of(eps, eps);
      assertEquals(reps.divide(reps), RealScalar.ONE);
    }
  }

  @Test
  public void testDivision2b() {
    double eps = Double.MIN_VALUE; // 5.562684646268010E-309
    Scalar reps = RealScalar.of(eps);
    Scalar ieps = ComplexScalar.of(0, eps);
    assertEquals(ieps.divide(reps), ComplexScalar.of(0, 1));
    assertEquals(reps.divide(ieps), ComplexScalar.of(0, -1));
    _assertDivideSymmetric(ieps, reps);
    _assertDivideSymmetric(reps, ieps);
  }

  @SuppressWarnings("unused")
  private static Scalar textbookInversion(Scalar s1) {
    ComplexScalarImpl c = (ComplexScalarImpl) s1;
    Scalar mag = c.real().multiply(c.real()).add(c.imag().multiply(c.imag()));
    return ComplexScalar.of(c.real().divide(mag), c.imag().negate().divide(mag));
  }

  @Test
  public void testInversion() {
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY).reciprocal(), RealScalar.ZERO);
    assertEquals(ComplexScalar.of(RealScalar.ZERO, DoubleScalar.NEGATIVE_INFINITY).reciprocal(), RealScalar.ZERO);
    assertEquals(ComplexScalar.of(RealScalar.ONE, DoubleScalar.POSITIVE_INFINITY).reciprocal(), RealScalar.ZERO);
    assertEquals(ComplexScalar.of(RealScalar.ONE, DoubleScalar.NEGATIVE_INFINITY).reciprocal(), RealScalar.ZERO);
    assertEquals(ComplexScalar.of(DoubleScalar.POSITIVE_INFINITY, RealScalar.ZERO).reciprocal(), RealScalar.ZERO);
    assertEquals(ComplexScalar.of(DoubleScalar.NEGATIVE_INFINITY, RealScalar.ZERO).reciprocal(), RealScalar.ZERO);
    assertEquals(ComplexScalar.of(DoubleScalar.POSITIVE_INFINITY, RealScalar.ONE).reciprocal(), RealScalar.ZERO);
    assertEquals(ComplexScalar.of(DoubleScalar.NEGATIVE_INFINITY, RealScalar.ONE).reciprocal(), RealScalar.ZERO);
    // mathematica also does not simplify 1 / (inf+inf*I)
    // assertEquals(ComplexScalar.of( //
    // DoubleScalar.POSITIVE_INFINITY, DoubleScalar.POSITIVE_INFINITY).reciprocal(), RealScalar.ZERO);
  }

  @Test
  public void testGaussScalarFail() {
    Tensor tensor = Tensors.fromString("{0.3, 1/3, 3+4*I, 1.2+3.4*I}");
    Scalar g = GaussScalar.of(1, 7);
    for (Tensor _x : tensor) {
      Scalar x = (Scalar) _x;
      assertThrows(Exception.class, () -> x.divide(g));
      assertThrows(Exception.class, () -> x.under(g));
      assertThrows(Exception.class, () -> x.multiply(g));
      assertThrows(Exception.class, () -> g.divide(x));
      assertThrows(Exception.class, () -> g.under(x));
    }
  }

  @Test
  public void testAddQuantityFail() {
    Scalar s1 = Quantity.of(1, "m");
    Scalar s2 = ComplexScalar.of(2, 3);
    assertThrows(TensorRuntimeException.class, () -> s1.add(s2));
    assertThrows(TensorRuntimeException.class, () -> s2.add(s1));
  }

  @Test
  public void testDivisionInf1() {
    _assertDivideSymmetric( //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY), //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY));
    _assertDivideSymmetric( //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.NEGATIVE_INFINITY), //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY));
    _assertDivideSymmetric( //
        ComplexScalar.of(DoubleScalar.POSITIVE_INFINITY, DoubleScalar.NEGATIVE_INFINITY), //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY));
    _assertDivideSymmetric( //
        ComplexScalar.of(DoubleScalar.POSITIVE_INFINITY, RealScalar.ZERO), //
        ComplexScalar.of(RealScalar.ZERO, DoubleScalar.POSITIVE_INFINITY));
  }
}
