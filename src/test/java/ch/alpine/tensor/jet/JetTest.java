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

class JetTest {
  @Test
  void testMultiply() {
    Scalar s1 = Serialization.copy(Jet.of(Tensors.vector(4, 1, 2)));
    Scalar s2 = Jet.of(Tensors.vector(2, 3, -1));
    Scalar scalar = s1.multiply(s2);
    Jet jetScalar = (Jet) scalar;
    assertEquals(jetScalar.derivatives(), Tensors.vector(8, 14, 6));
    assertThrows(NullPointerException.class, () -> s1.multiply(null));
  }

  @Test
  void testComplex() {
    Scalar s1 = Jet.of(Tensors.of(Complex.of(7, 5), Complex.of(4, 2)));
    Scalar s2 = Jet.of(Tensors.of(Complex.of(-2, 3), Complex.of(1, -1)));
    Scalar s12 = s1.multiply(s2);
    Scalar s21 = s2.multiply(s1);
    assertEquals(s12, s21);
  }

  @Test
  void testReciprocal() {
    Scalar s1 = Jet.of(Tensors.vector(4, 1, 2));
    Scalar reciprocal = s1.reciprocal();
    assertEquals(((Jet) reciprocal).derivatives(), Tensors.fromString("{1/4, -1/16, -3/32}"));
    Scalar neutral = s1.multiply(reciprocal);
    assertEquals(((Jet) neutral).derivatives(), UnitVector.of(3, 0));
  }

  @Test
  void testPower() {
    Scalar s1 = Jet.of(Tensors.vector(4, 1, 2, -3));
    Scalar scalar = Power.of(s1, 5);
    Jet jetScalar = (Jet) scalar;
    assertEquals(jetScalar.derivatives(), Tensors.vector(1024, 1280, 3840, 4800));
  }

  @Test
  void testMultiplex() {
    Scalar s1 = Jet.of(Tensors.vector(4, 1, 2, -3));
    ExactScalarQ.require(s1);
    Scalar s2 = N.DOUBLE.apply(s1);
    assertFalse(ExactScalarQ.of(s2));
  }

  @Test
  void testScalar() {
    Scalar s1 = Jet.identity(RealScalar.of(3), 4);
    Jet jetScalar = (Jet) s1;
    assertEquals(jetScalar.derivatives(), Tensors.vector(3, 1, 0, 0));
  }

  @Test
  void testNegate() {
    Scalar s1 = Jet.of(Tensors.vector(4, 1, 2, -3));
    Scalar s2 = RealScalar.of(3);
    Jet jetScalar = (Jet) s2.multiply(s1);
    assertEquals(jetScalar.derivatives(), Tensors.vector(12, 3, 6, -9));
  }

  @Test
  void testSqrt() {
    Scalar s1 = Jet.of(Tensors.vector(4, 2, 1, -3));
    Jet scalar = (Jet) Sqrt.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.derivatives(), Tensors.vector(2, 0.5, 0.125, -0.84375));
  }

  @Test
  void testExp() {
    Scalar s1 = Jet.of(Tensors.vector(4, 2, 0, -3));
    Jet scalar = (Jet) Exp.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.derivatives(), //
        Tensors.vector(54.598150033144236, 109.19630006628847, 218.39260013257694, 272.9907501657212));
  }

  @Test
  void testLog() {
    Scalar s1 = Jet.of(Tensors.vector(4, 2, 0, -3));
    Jet scalar = (Jet) Log.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.derivatives(), Tensors.vector(1.3862943611198906, 0.5, -0.25, -0.5));
  }

  @Test
  void testSin() {
    Scalar s1 = Jet.of(Tensors.vector(4, 2, 0, -3));
    Jet scalar = (Jet) Sin.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.derivatives(), //
        Tensors.vector(-0.7568024953079282, -1.3072872417272239, 3.027209981231713, 7.190079829499732));
  }

  @Test
  void testSinh() {
    Scalar s1 = Jet.of(Tensors.vector(4, 2, 0, -3));
    Jet scalar = (Jet) Sinh.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.derivatives(), //
        Tensors.vector(27.28991719712775, 54.61646567203297, 109.159668788511, 136.54116418008243));
  }

  @Test
  void testAbsSquared() {
    Scalar s1 = Jet.of(Tensors.vector(3, 1));
    Jet absSq = (Jet) AbsSquared.FUNCTION.apply(s1);
    Jet quadr = (Jet) s1.multiply(s1);
    assertEquals(absSq, quadr);
  }

  @Test
  void testArctanWithRealScalar() {
    // Jet for f(t) = t at t = 1 with 3rd-order derivatives: {1, 1, 0, 0}
    Jet u = Jet.of(Tensors.vector(1, 1, 0, 0));
    Scalar x = RealScalar.of(2);
    // g(t) = arctan(f(t) / x) = arctan(t / 2) at t = 1:
    // g(1) = arctan(1/2)
    // g'(1) = (1/2) / (1 + (1/2)^2) = 2/5
    // g''(1) = d/dt [ 2 / (4 + t^2) ] at t = 1 = -4t / (4 + t^2)^2 at t = 1 = -4 / 25
    Scalar actual = u.arcTan(x);
    Tensor expected = Tensors.of(ArcTan.FUNCTION.apply(Rational.HALF), //
        Rational.of(2, 5), Rational.of(-4, 25), Rational.of(-4, 125));
    assertEquals(Jet.of(expected), actual);
  }

  @Test
  void testArctanWithJetScalar() {
    // Both numerator and denominator depend on t:
    // num(t) = t -> {1, 1, 0}
    // den(t) = 2*t -> {2, 2, 0}
    // quotient q(t) = t / (2*t) = 1/2 -> {1/2, 0, 0}
    Jet num = Jet.of(Tensors.vector(1, 1, 0));
    Jet den = Jet.of(Tensors.vector(2, 2, 0));
    Scalar actual = num.arcTan(den);
    Tensor expected = Tensors.of(ArcTan.FUNCTION.apply(Rational.HALF), RealScalar.ZERO, RealScalar.ZERO);
    assertEquals(Jet.of(expected), actual);
  }

  @Test
  void testArctanEmpty() {
    Jet empty = new Jet(Tensors.empty());
    assertEquals(new Jet(Tensors.empty()), empty.arcTan(RealScalar.ONE));
  }

  @Test
  void testScalarFail() {
    assertThrows(Throw.class, () -> Jet.of(RealScalar.of(2)));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testJetScalarConstantFail(int n) {
    Jet jetScalar = Jet.identity(RealScalar.of(2), n);
    assertThrows(Throw.class, () -> Jet.identity(jetScalar, n));
  }

  @Test
  void testJetScalarHashSet() {
    Set<Scalar> set = new HashSet<>();
    for (int n = 3; n < 10; ++n)
      set.add(Jet.identity(RealScalar.of(n), 3));
    assertEquals(Jet.identity(Pi.VALUE, 3), Jet.identity(Pi.VALUE, 3));
    assertNotEquals(Jet.identity(Pi.VALUE, 2), Jet.identity(Pi.VALUE, 3));
    assertNotEquals(Jet.identity(Pi.VALUE, 3), Jet.identity(Pi.HALF, 3));
  }

  @Test
  void testMatrixFail() {
    assertThrows(Throw.class, () -> Jet.of(HilbertMatrix.of(3)));
  }

  @Test
  void testPolynomial() {
    Polynomial p0 = Polynomial.of(Tensors.vector(2, -3, -2, 5, -2, -1));
    Polynomial p1 = p0.derivative();
    Polynomial p2 = p1.derivative();
    List<Polynomial> ps = List.of(p0, p1, p2);
    Scalar x = RealScalar.of(4);
    Tensor fs = Tensor.of(ps.stream().map(p -> p.apply(x)));
    Jet xj = Jet.identity(x, 3);
    Jet fj = (Jet) p0.apply(xj);
    assertEquals(fs, fj.derivatives());
    ExactScalarQ.require(fj);
  }

  @Test
  void testNestFail() {
    Jet js = Jet.identity(RealScalar.of(2), 3);
    assertThrows(Throw.class, () -> Jet.of(Tensors.of(RealScalar.of(1), js)));
  }
}
