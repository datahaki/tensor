// code by jph
package ch.alpine.tensor.lie;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RandomQuaternion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.CauchyDistribution;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.LogNormalDistribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.KroneckerDelta;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.Sqrt;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class QuaternionTest extends TestCase {
  public void testContructQuantity() {
    Quaternion quaternion = Quaternion.of(Quantity.of(3, "m"), Tensors.fromString("{2[m],3[m],4[m]}"));
    Scalar abs = quaternion.abs();
    Tolerance.CHOP.requireClose(abs, Scalars.fromString("6.164414002968976[m]"));
    ExactScalarQ.require(quaternion);
    assertEquals(quaternion.conjugate(), Quaternion.of(Quantity.of(3, "m"), Tensors.fromString("{-2[m],-3[m],-4[m]}")));
    assertTrue(quaternion.sqrt() instanceof QuaternionImpl);
    Quaternion actual = quaternion.reciprocal();
    Quaternion expect = Quaternion.of(Scalars.fromString("3/38[m^-1]"), Tensors.fromString("{-1/19[m^-1], -3/38[m^-1], -2/19[m^-1]}"));
    assertEquals(expect, actual);
    AssertFail.of(() -> quaternion.exp());
    AssertFail.of(() -> quaternion.log());
  }

  public void testComplex() {
    Quaternion q1 = Quaternion.of( //
        ComplexScalar.of(1, 2), //
        ComplexScalar.of(2, 3), ComplexScalar.of(-1, 8), ComplexScalar.of(7, 9));
    // System.out.println(q1.divide(q1));
    // System.out.println(q1.under(q1));
    Quaternion q2 = Quaternion.of( //
        ComplexScalar.of(-1, 10), //
        ComplexScalar.of(-1, 4), ComplexScalar.of(0, 2), ComplexScalar.of(1, -33));
    // System.out.println(q1);
    // System.out.println(q2);
    // Quaternion q12 =
    q1.multiply(q2);
    // System.out.println(q12);
    // Quaternion q11 = q1.multiply(q1);
    // ExactScalarQ.require(q12);
    // System.out.println(q12);
    // assertEquals(q1.under(q12), q2);
    // assertEquals(q12.divide(q2), q1);
    // assertEquals(q11.divide(q1), q1);
  }

  public void testNoRef() {
    Tensor xya = Tensors.vector(1, 2, 3);
    Quaternion quaternion = Quaternion.of(RealScalar.ONE, xya);
    xya.set(Scalar::zero, Tensor.ALL);
    Chop.NONE.requireAllZero(xya);
    assertEquals(quaternion.xyz(), Tensors.vector(1, 2, 3));
  }

  public void testContruct() {
    Scalar c1 = ComplexScalar.of(1, 3);
    Scalar q1 = Quaternion.of(1, 3, 0, 0);
    assertEquals(q1, q1);
    assertFalse(c1.equals(Quaternion.of(1, 3, 1, 0)));
  }

  public void testAdd() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    Scalar qr = Quaternion.of(9, 3, -1, 3);
    Scalar qb = Quaternion.of(10, 6, -3, 5);
    assertEquals(q1.add(qr), qb);
    assertEquals(qb.subtract(qr), q1);
    assertEquals(qr.add(q1), qb);
    assertEquals(qb.subtract(q1), qr);
  }

  public void testAbs() {
    Quaternion quaternion = Quaternion.of(2, 0, -6, 3);
    Scalar norm = quaternion.abs();
    assertEquals(norm, RealScalar.of(7));
    Quaternion divide = quaternion.divide(norm);
    assertEquals(divide.abs(), RealScalar.ONE);
  }

  public void testMultiply() {
    Scalar q1 = Quaternion.of(2, 0, -6, 3);
    Scalar q2 = Quaternion.of(1, 3, -2, 2);
    assertEquals(q1.multiply(q2), Quaternion.of(-16, 0, -1, 25));
    assertEquals(q2.multiply(q1), Quaternion.of(-16, 12, -19, -11));
    assertEquals(q1.divide(q1), Quaternion.ONE);
    assertEquals(q2.divide(q2), Quaternion.ONE);
  }

  public void testMultiplyComplex() {
    Scalar c1 = ComplexScalar.of(2, 3);
    Scalar q1 = Quaternion.of(7, 9, -6, 4);
    Scalar r1 = c1.multiply(q1);
    assertEquals(r1, Quaternion.of(-13, 39, 0, 26));
  }

  public void testReciprocal() {
    Scalar q1 = Quaternion.of(2, 0, -6, 3);
    Scalar q2 = Quaternion.of(1, 3, -2, 2);
    assertEquals(q1.reciprocal().multiply(q1), Quaternion.ONE);
    assertEquals(q2.reciprocal().multiply(q2), Quaternion.ONE);
  }

  public void testConjugate() {
    Scalar s = Conjugate.of(Quaternion.of(1, 2, 3, 4));
    assertEquals(s, Quaternion.of(1, -2, -3, -4));
  }

  public void testSqrt() {
    Tensor arg = RandomVariate.of(NormalDistribution.standard(), 4);
    Scalar q = Quaternion.of(arg.Get(0), arg.extract(1, 4));
    Scalar r = Sqrt.of(q);
    Scalar r2 = r.multiply(r);
    Tolerance.CHOP.requireClose(r2, q);
  }

  public void testSqrt0() {
    Tensor arg = RandomVariate.of(NormalDistribution.standard(), 4);
    Scalar q = Quaternion.of(RealScalar.ZERO, arg.extract(1, 4));
    Scalar r = Sqrt.of(q);
    Scalar r2 = r.multiply(r);
    Tolerance.CHOP.requireClose(r2, q);
  }

  public void testSome() {
    Scalar q1 = Quaternion.of(1, 23, 4, 5);
    Scalar q2 = Quaternion.of(1, 2, 4, 5);
    Scalar q3 = Quaternion.of(1, 23, 3, 5);
    Scalar q4 = Quaternion.of(1, 23, 4, 4);
    Tensor v = Tensors.of(q1, q2, q3, q4);
    int n = v.length();
    Tensor matrix = Tensors.matrix((i, j) -> KroneckerDelta.of(v.Get(i), v.Get(j)), n, n);
    assertEquals(matrix, IdentityMatrix.of(n));
  }

  private static Scalar _createQ(Tensor vec) {
    VectorQ.requireLength(vec, 4);
    return Quaternion.of(vec.Get(0), vec.Get(1), vec.Get(2), vec.Get(3));
  }

  public void testNormVsAbs() {
    Distribution distribution = CauchyDistribution.standard();
    Tensor vec = RandomVariate.of(distribution, 4);
    Scalar q1 = _createQ(vec);
    Scalar nrm = Vector2Norm.of(vec);
    Scalar abs = Abs.of(q1);
    Tolerance.CHOP.requireClose(nrm, abs);
  }

  public void testExactScalarQ() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    ExactScalarQ.require(q1);
    Scalar q2 = Quaternion.of(1, 3, -2., 2);
    assertFalse(ExactScalarQ.of(q2));
  }

  public void testOne() {
    Scalar scalar = Quaternion.of(11, 33, -28, 29);
    assertEquals(scalar.one().multiply(scalar), scalar);
    assertEquals(scalar.multiply(scalar.one()), scalar);
  }

  public void testN() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    ExactScalarQ.require(q1);
    Scalar n1 = N.DOUBLE.apply(q1);
    assertFalse(ExactScalarQ.of(n1));
    assertEquals(n1.toString(), "{\"w\": 1.0, \"xyz\": {3.0, -2.0, 2.0}}");
  }

  public void testN2() {
    Scalar q1 = Quaternion.of(1, 3, -2, 2);
    ExactScalarQ.require(q1);
    Scalar n1 = N.DECIMAL64.apply(q1);
    assertFalse(ExactScalarQ.of(n1));
    assertEquals(n1.toString(), "{\"w\": 1, \"xyz\": {3, -2, 2}}");
  }

  public void testExpLog() {
    Quaternion quaternion = Quaternion.of(0.1, 0.3, 0.2, -0.3);
    Quaternion exp = quaternion.exp();
    Quaternion log = exp.log();
    Chop._14.requireClose(quaternion, log);
  }

  public void testExpLogRandom() {
    Distribution distribution = NormalDistribution.of(0, 0.3);
    Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Quaternion exp = quaternion.exp();
    Quaternion log = exp.log();
    Tolerance.CHOP.requireClose(quaternion, log);
  }

  public void testLogExpRandom() {
    Distribution distribution = NormalDistribution.of(0, 2.3);
    Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Quaternion log = quaternion.log();
    Quaternion exp = log.exp();
    Tolerance.CHOP.requireClose(quaternion, exp);
  }

  public void testSignAbsRandom() {
    Distribution distribution = LogNormalDistribution.standard();
    Quaternion quaternion = Quaternion.of(RandomVariate.of(distribution), RandomVariate.of(distribution, 3));
    Scalar sign = Sign.FUNCTION.apply(quaternion);
    Scalar abs = Abs.FUNCTION.apply(quaternion);
    Tolerance.CHOP.requireClose(sign.multiply(abs), quaternion);
  }

  public void testDivideUnder() {
    Quaternion q1 = RandomQuaternion.get();
    Quaternion q2 = RandomQuaternion.get();
    if (RandomQuaternion.nonCommute(q1, q2)) {
      Quaternion q12 = q1.multiply(q2);
      assertEquals(q12.divide(q2), q1);
      assertEquals(q1.under(q12), q2);
      assertFalse(q12.divide(q1).equals(q2));
      assertFalse(q2.under(q12).equals(q1));
    }
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Quaternion.of(RealScalar.ONE, HilbertMatrix.of(3, 3)));
    AssertFail.of(() -> Quaternion.of(RealScalar.ONE, RealScalar.of(4)));
  }

  public void testNull1Fail() {
    AssertFail.of(() -> Quaternion.of(null, Tensors.vector(1, 2, 3)));
    AssertFail.of(() -> Quaternion.of(RealScalar.ONE, null));
  }

  public void testNull2Fail() {
    AssertFail.of(() -> Quaternion.of(null, RealScalar.ONE, RealScalar.of(2), RealScalar.of(8)));
  }

  public void testNull2bFail() {
    AssertFail.of(() -> Quaternion.of(RealScalar.ONE, null, RealScalar.of(2), RealScalar.of(8)));
  }

  public void testNull3Fail() {
    AssertFail.of(() -> Quaternion.of(1, null, 2, 3));
  }

  public void testFormatFail() {
    AssertFail.of(() -> Quaternion.of(RealScalar.ONE, Tensors.vector(1, 2, 3, 4)));
  }
}
