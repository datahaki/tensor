// code by jph
package ch.alpine.tensor.lie.rot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RandomQuaternion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.FiniteScalarQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.CauchyDistribution;
import ch.alpine.tensor.pdf.c.LogNormalDistribution;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.KroneckerDelta;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Floor;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.Tan;

class QuaternionTest {
  @Test
  void testContructQuantity() {
    Quaternion quaternion = Quaternion.of(Quantity.of(3, "m"), Tensors.fromString("{2[m],3[m],4[m]}"));
    assertEquals(quaternion.multiply(quaternion.one()), quaternion);
    assertEquals(quaternion.one().multiply(quaternion), quaternion);
    Scalar abs = quaternion.abs();
    Tolerance.CHOP.requireClose(abs, Scalars.fromString("6.164414002968976[m]"));
    ExactScalarQ.require(quaternion);
    assertEquals(quaternion.conjugate(), Quaternion.of(Quantity.of(3, "m"), Tensors.fromString("{-2[m],-3[m],-4[m]}")));
    assertInstanceOf(QuaternionImpl.class, quaternion.sqrt());
    Quaternion actual = quaternion.reciprocal();
    Quaternion expect = Quaternion.of(Scalars.fromString("3/38[m^-1]"), Tensors.fromString("{-1/19[m^-1], -3/38[m^-1], -2/19[m^-1]}"));
    assertEquals(expect, actual);
    assertThrows(Throw.class, quaternion::exp);
    assertThrows(Throw.class, quaternion::log);
  }

  @Test
  void testComplex() {
    Quaternion q1 = Quaternion.of( //
        ComplexScalar.of(1, 2), //
        ComplexScalar.of(2, 3), ComplexScalar.of(-1, 8), ComplexScalar.of(7, 9));
    Quaternion q2 = Quaternion.of( //
        ComplexScalar.of(-1, 10), //
        ComplexScalar.of(-1, 4), ComplexScalar.of(0, 2), ComplexScalar.of(1, -33));
    Quaternion res = q1.multiply(q2);
    ExactScalarQ.require(res);
  }

  @Test
  void testNoRef() {
    Tensor xya = Tensors.vector(1, 2, 3);
    Quaternion quaternion = Quaternion.of(RealScalar.ONE, xya);
    xya.set(Scalar::zero, Tensor.ALL);
    Chop.NONE.requireAllZero(xya);
    assertEquals(quaternion.xyz(), Tensors.vector(1, 2, 3));
  }

  @Test
  void testContruct() {
    Scalar c1 = ComplexScalar.of(1, 3);
    Scalar q1 = Quaternion.of(1, 3, 0, 0);
    assertEquals(q1, q1);
    assertNotEquals(c1, Quaternion.of(1, 3, 1, 0));
  }

  @Test
  void testAdd() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    Scalar qr = Quaternion.of(9, 3, -1, 3);
    Scalar qb = Quaternion.of(10, 6, -3, 5);
    assertEquals(q1.add(qr), qb);
    assertEquals(qb.subtract(qr), q1);
    assertEquals(qr.add(q1), qb);
    assertEquals(qb.subtract(q1), qr);
  }

  @Test
  void testAbs() {
    Quaternion quaternion = Quaternion.of(2, 0, -6, 3);
    Scalar norm = quaternion.abs();
    assertEquals(norm, RealScalar.of(7));
    Quaternion divide = quaternion.divide(norm);
    assertEquals(divide.abs(), RealScalar.ONE);
  }

  @Test
  void testMultiply() {
    Scalar q1 = Quaternion.of(2, 0, -6, 3);
    Scalar q2 = Quaternion.of(1, 3, -2, 2);
    assertEquals(q1.multiply(q2), Quaternion.of(-16, 0, -1, 25));
    assertEquals(q2.multiply(q1), Quaternion.of(-16, 12, -19, -11));
    assertEquals(q1.divide(q1), Quaternion.ONE);
    assertEquals(q2.divide(q2), Quaternion.ONE);
  }

  @Test
  void testMultiplyComplex() {
    Scalar c1 = ComplexScalar.of(2, 3);
    Scalar q1 = Quaternion.of(7, 9, -6, 4);
    Scalar r1 = c1.multiply(q1);
    assertEquals(r1, Quaternion.of(-13, 39, 0, 26));
  }

  @Test
  void testReciprocal() {
    Scalar q1 = Quaternion.of(2, 0, -6, 3);
    Scalar q2 = Quaternion.of(1, 3, -2, 2);
    assertEquals(q1.reciprocal().multiply(q1), Quaternion.ONE);
    assertEquals(q2.reciprocal().multiply(q2), Quaternion.ONE);
  }

  @Test
  void testConjugate() {
    Scalar s = Conjugate.FUNCTION.apply(Quaternion.of(1, 2, 3, 4));
    assertEquals(s, Quaternion.of(1, -2, -3, -4));
  }

  @ParameterizedTest
  @MethodSource(value = "test.bulk.TestDistributions#distributions")
  void testSqrt(Distribution distribution) {
    Tensor arg = RandomVariate.of(distribution, 4);
    Scalar q = Quaternion.of(arg.Get(0), arg.extract(1, 4));
    Scalar r = Sqrt.FUNCTION.apply(q);
    Scalar r2 = r.multiply(r);
    Tolerance.CHOP.requireClose(r2, q);
  }

  @ParameterizedTest
  @MethodSource(value = "test.bulk.TestDistributions#distributions")
  void testSqrt0(Distribution distribution) {
    Tensor arg = RandomVariate.of(distribution, 4);
    Scalar q = Quaternion.of(RealScalar.ZERO, arg.extract(1, 4));
    Scalar r = Sqrt.FUNCTION.apply(q);
    Scalar r2 = r.multiply(r);
    Tolerance.CHOP.requireClose(r2, q);
  }

  @Test
  void testSome() {
    Scalar q1 = Quaternion.of(1, 23, 4, 5);
    Scalar q2 = Quaternion.of(1, 2, 4, 5);
    Scalar q3 = Quaternion.of(1, 23, 3, 5);
    Scalar q4 = Quaternion.of(1, 23, 4, 4);
    Tensor v = Tensors.of(q1, q2, q3, q4);
    int n = v.length();
    Tensor matrix = Tensors.matrix((i, j) -> KroneckerDelta.of(v.Get(i), v.Get(j)), n, n);
    assertEquals(matrix, IdentityMatrix.of(n));
  }

  @Test
  void testTan() {
    Scalar q = Quaternion.of(2, 3, -6, 3);
    Scalar tan = Tan.FUNCTION.apply(q);
    FiniteScalarQ.require(tan);
  }

  private static Scalar _createQ(Tensor vec) {
    VectorQ.requireLength(vec, 4);
    return Quaternion.of(vec.Get(0), vec.Get(1), vec.Get(2), vec.Get(3));
  }

  @Test
  void testNormVsAbs() {
    Distribution distribution = CauchyDistribution.standard();
    Tensor vec = RandomVariate.of(distribution, 4);
    Scalar q1 = _createQ(vec);
    Scalar nrm = Vector2Norm.of(vec);
    Scalar abs = Abs.FUNCTION.apply(q1);
    Tolerance.CHOP.requireClose(nrm, abs);
  }

  @Test
  void testExactScalarQ() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    ExactScalarQ.require(q1);
    Scalar q2 = Quaternion.of(1, 3, -2., 2);
    assertFalse(ExactScalarQ.of(q2));
  }

  @Test
  void testOne() {
    Scalar scalar = Quaternion.of(11, 33, -28, 29);
    assertEquals(scalar.one().multiply(scalar), scalar);
    assertEquals(scalar.multiply(scalar.one()), scalar);
  }

  @Test
  void testN() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    ExactScalarQ.require(q1);
    Scalar n1 = N.DOUBLE.apply(q1);
    assertFalse(ExactScalarQ.of(n1));
    // assertEquals(n1.toString(), "{\"w\": 1.0, \"xyz\": {3.0, -2.0, 2.0}}");
    assertEquals(n1.toString(), "Quaternion[1.0, {3.0, -2.0, 2.0}]");
  }

  @Test
  void testN2() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    ExactScalarQ.require(q1);
    Scalar n1 = N.DECIMAL64.apply(q1);
    assertFalse(ExactScalarQ.of(n1));
    // assertEquals(n1.toString(), "{\"w\": 1, \"xyz\": {3, -2, 2}}");
    assertEquals(n1.toString(), "Quaternion[1, {3, -2, 2}]");
  }

  @Test
  void testExpLog() {
    Quaternion quaternion = Quaternion.of(0.1, 0.3, 0.2, -0.3);
    Quaternion exp = quaternion.exp();
    Quaternion log = exp.log();
    Chop._14.requireClose(quaternion, log);
  }

  @Test
  void testExpLogRandom() {
    Distribution distribution = NormalDistribution.of(0, 0.3);
    Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Quaternion exp = quaternion.exp();
    Quaternion log = exp.log();
    Tolerance.CHOP.requireClose(quaternion, log);
  }

  @Test
  void testLogExpRandom() {
    Distribution distribution = NormalDistribution.of(0, 2.3);
    Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Quaternion log = quaternion.log();
    Quaternion exp = log.exp();
    Tolerance.CHOP.requireClose(quaternion, exp);
  }

  @Test
  void testSignAbsRandom() {
    Distribution distribution = LogNormalDistribution.standard();
    Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Scalar sign = Sign.FUNCTION.apply(quaternion);
    Scalar abs = Abs.FUNCTION.apply(quaternion);
    Tolerance.CHOP.requireClose(sign.multiply(abs), quaternion);
  }

  @Test
  void testRound() {
    Quaternion quaternion = Quaternion.of(3.9, 2.3, 1.8, -1.3);
    assertEquals(Round.FUNCTION.apply(quaternion), Quaternion.of(4, 2, 2, -1));
    assertEquals(Ceiling.FUNCTION.apply(quaternion), Quaternion.of(4, 3, 2, -1));
    assertEquals(Floor.FUNCTION.apply(quaternion), Quaternion.of(3, 2, 1, -2));
  }

  @Test
  void testDivideUnder() {
    Quaternion q1 = RandomQuaternion.get();
    Quaternion q2 = RandomQuaternion.get();
    assumeTrue(RandomQuaternion.nonCommute(q1, q2));
    Quaternion q12 = q1.multiply(q2);
    assertEquals(q12.divide(q2), q1);
    assertEquals(q1.under(q12), q2);
    assertNotEquals(q12.divide(q1), q2);
    assertNotEquals(q2.under(q12), q1);
  }

  @Test
  void testMatrixFail() {
    assertThrows(Throw.class, () -> Quaternion.of(RealScalar.ONE, HilbertMatrix.of(3, 3)));
    assertThrows(Throw.class, () -> Quaternion.of(RealScalar.ONE, RealScalar.of(4)));
  }

  @Test
  void testNull1Fail() {
    assertThrows(NullPointerException.class, () -> Quaternion.of(null, Tensors.vector(1, 2, 3)));
    assertThrows(NullPointerException.class, () -> Quaternion.of(RealScalar.ONE, null));
  }

  @Test
  void testNull2Fail() {
    assertThrows(NullPointerException.class, () -> Quaternion.of(null, RealScalar.ONE, RealScalar.of(2), RealScalar.of(8)));
  }

  @Test
  void testNull2bFail() {
    assertThrows(NullPointerException.class, () -> Quaternion.of(RealScalar.ONE, null, RealScalar.of(2), RealScalar.of(8)));
  }

  @Test
  void testNull3Fail() {
    assertThrows(NullPointerException.class, () -> Quaternion.of(1, null, 2, 3));
  }

  @Test
  void testFormatFail() {
    assertThrows(Throw.class, () -> Quaternion.of(RealScalar.ONE, Tensors.vector(1, 2, 3, 4)));
  }
}
