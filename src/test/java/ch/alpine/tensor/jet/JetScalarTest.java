// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.Complex;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.ply.Polynomial;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.ArcTan;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinh;

class JetScalarTest {
  @Test
  void testMultiply() {
    Scalar s1 = Serialization.copy(JetScalar.of(Tensors.vector(4, 1, 2)));
    Scalar s2 = JetScalar.of(Tensors.vector(2, 3, -1));
    Scalar scalar = s1.multiply(s2);
    JetScalar jetScalar = (JetScalar) scalar;
    assertEquals(jetScalar.jet(), Tensors.vector(8, 14, 6));
    assertThrows(NullPointerException.class, () -> s1.multiply(null));
  }

  @Test
  void testComplex() {
    Scalar s1 = JetScalar.of(Tensors.of(Complex.of(7, 5), Complex.of(4, 2)));
    Scalar s2 = JetScalar.of(Tensors.of(Complex.of(-2, 3), Complex.of(1, -1)));
    Scalar s12 = s1.multiply(s2);
    Scalar s21 = s2.multiply(s1);
    assertEquals(s12, s21);
  }

  @Test
  void testReciprocal() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2));
    Scalar reciprocal = s1.reciprocal();
    assertEquals(((JetScalar) reciprocal).jet(), Tensors.fromString("{1/4, -1/16, -3/32}"));
    Scalar neutral = s1.multiply(reciprocal);
    assertEquals(((JetScalar) neutral).jet(), UnitVector.of(3, 0));
  }

  @Test
  void testPower() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar scalar = Power.of(s1, 5);
    JetScalar jetScalar = (JetScalar) scalar;
    assertEquals(jetScalar.jet(), Tensors.vector(1024, 1280, 3840, 4800));
  }

  @Test
  void testMultiplex() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    ExactScalarQ.require(s1);
    Scalar s2 = N.DOUBLE.apply(s1);
    assertFalse(ExactScalarQ.of(s2));
  }

  @Test
  void testScalar() {
    Scalar s1 = JetScalar.identity(RealScalar.of(3), 4);
    JetScalar jetScalar = (JetScalar) s1;
    assertEquals(jetScalar.jet(), Tensors.vector(3, 1, 0, 0));
  }

  @Test
  void testNegate() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar s2 = RealScalar.of(3);
    JetScalar jetScalar = (JetScalar) s2.multiply(s1);
    assertEquals(jetScalar.jet(), Tensors.vector(12, 3, 6, -9));
  }

  @Test
  void testSqrt() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 1, -3));
    JetScalar scalar = (JetScalar) Sqrt.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.jet(), Tensors.vector(2, 0.5, 0.125, -0.84375));
  }

  @Test
  void testExp() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Exp.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.jet(), //
        Tensors.vector(54.598150033144236, 109.19630006628847, 218.39260013257694, 272.9907501657212));
  }

  @Test
  void testLog() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Log.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.jet(), Tensors.vector(1.3862943611198906, 0.5, -0.25, -0.5));
  }

  @Test
  void testSin() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Sin.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.jet(), //
        Tensors.vector(-0.7568024953079282, -1.3072872417272239, 3.027209981231713, 7.190079829499732));
  }

  @Test
  void testSinh() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Sinh.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.jet(), //
        Tensors.vector(27.28991719712775, 54.61646567203297, 109.159668788511, 136.54116418008243));
  }

  @Test
  void testAbsSquared() {
    Scalar s1 = JetScalar.of(Tensors.vector(3, 1));
    JetScalar absSq = (JetScalar) AbsSquared.FUNCTION.apply(s1);
    JetScalar quadr = (JetScalar) s1.multiply(s1);
    assertEquals(absSq, quadr);
  }

  @Test
  void testArctanWithRealScalar() {
    // Jet for f(t) = t at t = 1 with 3rd-order derivatives: {1, 1, 0, 0}
    JetScalar u = JetScalar.of(Tensors.vector(1, 1, 0, 0));
    Scalar x = RealScalar.of(2);
    // g(t) = arctan(f(t) / x) = arctan(t / 2) at t = 1:
    // g(1) = arctan(1/2)
    // g'(1) = (1/2) / (1 + (1/2)^2) = 2/5
    // g''(1) = d/dt [ 2 / (4 + t^2) ] at t = 1 = -4t / (4 + t^2)^2 at t = 1 = -4 / 25
    Scalar actual = u.arcTan(x);
    Tensor expected = Tensors.of(ArcTan.FUNCTION.apply(Rational.HALF), //
        Rational.of(2, 5), Rational.of(-4, 25), Rational.of(-4, 125));
    assertEquals(JetScalar.of(expected), actual);
  }

  @Test
  void testArctanWithJetScalar() {
    // Both numerator and denominator depend on t:
    // num(t) = t -> {1, 1, 0}
    // den(t) = 2*t -> {2, 2, 0}
    // quotient q(t) = t / (2*t) = 1/2 -> {1/2, 0, 0}
    JetScalar num = JetScalar.of(Tensors.vector(1, 1, 0));
    JetScalar den = JetScalar.of(Tensors.vector(2, 2, 0));
    Scalar actual = num.arcTan(den);
    Tensor expected = Tensors.of(ArcTan.FUNCTION.apply(Rational.HALF), RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(JetScalar.of(expected), actual);
  }

  @Test
  void testArctanEmpty() {
    JetScalar empty = new JetScalar(Tensors.empty());
    assertEquals(new JetScalar(Tensors.empty()), empty.arcTan(RealScalar.ONE));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> JetScalar.of(RealScalar.of(2)));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testJetScalarConstantFail(int n) {
    JetScalar jetScalar = JetScalar.identity(RealScalar.of(2), n);
    assertThrows(Throw.class, () -> JetScalar.identity(jetScalar, n));
  }

  @Test
  void testJetScalarHashSet() {
    Set<Scalar> set = new HashSet<>();
    for (int n = 3; n < 10; ++n)
      set.add(JetScalar.identity(RealScalar.of(n), 3));
    assertEquals(JetScalar.identity(Pi.VALUE, 3), JetScalar.identity(Pi.VALUE, 3));
    assertNotEquals(JetScalar.identity(Pi.VALUE, 2), JetScalar.identity(Pi.VALUE, 3));
    assertNotEquals(JetScalar.identity(Pi.VALUE, 3), JetScalar.identity(Pi.HALF, 3));
  }

  @Test
  void testMatrixFail() {
    assertThrows(Throw.class, () -> JetScalar.of(HilbertMatrix.of(3)));
  }

  @Test
  void testPolynomial() {
    Polynomial p0 = Polynomial.of(Tensors.vector(2, -3, -2, 5, -2, -1));
    Polynomial p1 = p0.derivative();
    Polynomial p2 = p1.derivative();
    List<Polynomial> ps = List.of(p0, p1, p2);
    Scalar x = RealScalar.of(4);
    Tensor fs = Tensor.of(ps.stream().map(p -> p.apply(x)));
    JetScalar xj = JetScalar.identity(x, 3);
    JetScalar fj = (JetScalar) p0.apply(xj);
    assertEquals(fs, fj.jet());
    ExactScalarQ.require(fj);
  }

  @Test
  void testNestFail() {
    JetScalar js = JetScalar.identity(RealScalar.of(2), 3);
    assertThrows(Throw.class, () -> JetScalar.of(Tensors.of(RealScalar.of(1), js)));
  }
}
