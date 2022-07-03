// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.num.Polynomial;
import ch.alpine.tensor.sca.AbsSquared;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.exp.Log;
import ch.alpine.tensor.sca.pow.Power;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Sin;
import ch.alpine.tensor.sca.tri.Sinh;

class JetScalarTest {
  @Test
  void testMultiply() throws ClassNotFoundException, IOException {
    Scalar s1 = Serialization.copy(JetScalar.of(Tensors.vector(4, 1, 2)));
    Scalar s2 = JetScalar.of(Tensors.vector(2, 3, -1));
    Scalar scalar = s1.multiply(s2);
    JetScalar jetScalar = (JetScalar) scalar;
    assertEquals(jetScalar.vector(), Tensors.vector(8, 14, 6));
    assertThrows(NullPointerException.class, () -> s1.multiply(null));
  }

  @Test
  void testComplex() {
    Scalar s1 = JetScalar.of(Tensors.of(ComplexScalar.of(7, 5), ComplexScalar.of(4, 2)));
    Scalar s2 = JetScalar.of(Tensors.of(ComplexScalar.of(-2, 3), ComplexScalar.of(1, -1)));
    Scalar s12 = s1.multiply(s2);
    Scalar s21 = s2.multiply(s1);
    assertEquals(s12, s21);
  }

  @Test
  void testReciprocal() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2));
    Scalar reciprocal = s1.reciprocal();
    assertEquals(((JetScalar) reciprocal).vector(), Tensors.fromString("{1/4, -1/16, -3/32}"));
    Scalar neutral = s1.multiply(reciprocal);
    assertEquals(((JetScalar) neutral).vector(), UnitVector.of(3, 0));
  }

  @Test
  void testPower() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar scalar = Power.of(s1, 5);
    JetScalar jetScalar = (JetScalar) scalar;
    assertEquals(jetScalar.vector(), Tensors.vector(1024, 1280, 3840, 4800));
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
    Scalar s1 = JetScalar.of(RealScalar.of(3), 4);
    JetScalar jetScalar = (JetScalar) s1;
    assertEquals(jetScalar.vector(), Tensors.vector(3, 1, 0, 0));
  }

  @Test
  void testNegate() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 1, 2, -3));
    Scalar s2 = RealScalar.of(3);
    JetScalar jetScalar = (JetScalar) s2.multiply(s1);
    assertEquals(jetScalar.vector(), Tensors.vector(12, 3, 6, -9));
  }

  @Test
  void testSqrt() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 1, -3));
    JetScalar scalar = (JetScalar) Sqrt.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), Tensors.vector(2, 0.5, 0.125, -0.84375));
  }

  @Test
  void testExp() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Exp.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(54.598150033144236, 109.19630006628847, 218.39260013257694, 272.9907501657212));
  }

  @Test
  void testLog() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Log.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), Tensors.vector(1.3862943611198906, 0.5, -0.25, -0.5));
  }

  @Test
  void testSin() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Sin.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(-0.7568024953079282, -1.3072872417272239, 3.027209981231713, 7.190079829499732));
  }

  @Test
  void testSinh() {
    Scalar s1 = JetScalar.of(Tensors.vector(4, 2, 0, -3));
    JetScalar scalar = (JetScalar) Sinh.FUNCTION.apply(s1);
    Chop._10.requireClose(scalar.vector(), //
        Tensors.vector(27.28991719712775, 54.61646567203297, 109.159668788511, 136.54116418008243));
  }

  @Test
  void testAbsSquared() {
    Scalar s1 = JetScalar.of(Tensors.vector(3, 1));
    JetScalar absSq = (JetScalar) AbsSquared.FUNCTION.apply(s1);
    JetScalar quadr = (JetScalar) s1.multiply(s1);
    assertEquals(absSq, quadr);
  }
  // public void testArcTan() {
  // Scalar s1 = JetScalar.of(Tensors.vector(3, 1));
  // JetScalar scalar = (JetScalar) ArcTan.of(s1, RealScalar.of(1));
  // System.out.println(scalar);
  // }

  @Test
  void testScalarFail() {
    assertThrows(TensorRuntimeException.class, () -> JetScalar.of(RealScalar.of(2)));
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3 })
  void testJetScalarConstantFail(int n) {
    JetScalar jetScalar = JetScalar.of(RealScalar.of(2), n);
    assertThrows(TensorRuntimeException.class, () -> JetScalar.of(jetScalar, n));
  }

  @Test
  void testJetScalarHashSet() {
    Set<Scalar> set = new HashSet<>();
    for (int n = 3; n < 10; ++n)
      set.add(JetScalar.of(RealScalar.of(n), 3));
    assertEquals(JetScalar.of(Pi.VALUE, 3), JetScalar.of(Pi.VALUE, 3));
    assertNotEquals(JetScalar.of(Pi.VALUE, 2), JetScalar.of(Pi.VALUE, 3));
    assertNotEquals(JetScalar.of(Pi.VALUE, 3), JetScalar.of(Pi.HALF, 3));
  }

  @Test
  void testMatrixFail() {
    assertThrows(TensorRuntimeException.class, () -> JetScalar.of(HilbertMatrix.of(3)));
  }

  @Test
  void testPolynomial() {
    Polynomial p0 = Polynomial.of(Tensors.vector(2, -3, -2, 5, -2, -1));
    Polynomial p1 = p0.derivative();
    Polynomial p2 = p1.derivative();
    List<Polynomial> ps = List.of(p0, p1, p2);
    Scalar x = RealScalar.of(4);
    Tensor fs = Tensor.of(ps.stream().map(p -> p.apply(x)));
    JetScalar xj = JetScalar.of(x, 3);
    JetScalar fj = (JetScalar) p0.apply(xj);
    assertEquals(fs, fj.vector());
    ExactScalarQ.require(fj);
  }

  @Test
  void testNestFail() {
    JetScalar js = JetScalar.of(RealScalar.of(2), 3);
    assertThrows(TensorRuntimeException.class, () -> JetScalar.of(Tensors.of(RealScalar.of(1), js)));
  }
}
