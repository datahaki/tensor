// code by jph
package ch.alpine.tensor.nrm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.pow.Sqrt;
import ch.alpine.tensor.sca.tri.ArcTan;

class HypotTest {
  private static void _checkPair(double x, double y) {
    Scalar res = Hypot.of(RealScalar.of(x), RealScalar.of(y));
    double jav = Math.hypot(x, y);
    Chop._17.requireClose(res, RealScalar.of(jav));
  }

  private static void checkPair(double x, double y) {
    _checkPair(x, y);
    _checkPair(y, x);
    _checkPair(x, -y);
    _checkPair(y, -x);
    _checkPair(-x, y);
    _checkPair(-y, x);
    _checkPair(-x, -y);
    _checkPair(-y, -x);
  }

  @Test
  public void testBasic() {
    checkPair(1e-300, 1e-300);
    checkPair(0, 1e-300);
    checkPair(0, 0);
    checkPair(1, 1);
    checkPair(Math.nextDown(0.0), 0);
    checkPair(Math.nextDown(0.f), 0);
    checkPair(Math.nextUp(0.0), 0);
    checkPair(Math.nextUp(0.f), 0);
  }

  private static void checkVectorExact(Tensor vec) {
    Scalar hyp = Hypot.ofVector(vec);
    assertInstanceOf(RationalScalar.class, hyp);
    Scalar nrm = Vector2Norm.of(vec);
    assertInstanceOf(RationalScalar.class, nrm);
    assertEquals(hyp, nrm);
  }

  @Test
  public void testExact3() {
    int[][] array = new int[][] { //
        { 1, 4, 8 }, { 2, 3, 6 }, { 2, 5, 14 }, { 2, 6, 9 }, { 2, 8, 16 }, //
        { 2, 10, 11 }, { 3, 4, 12 }, { 4, 6, 12 }, { 4, 12, 18 }, { 4, 13, 16 }, //
        { 6, 9, 18 }, { 6, 10, 15 }, { 6, 13, 18 }, { 7, 14, 22 }, { 8, 9, 12 }, //
        { 8, 11, 16 }, { 9, 12, 20 } };
    for (Tensor vec : Tensors.matrixInt(array))
      checkVectorExact(vec);
  }

  @Test
  public void testTuple() {
    int[][] array = new int[][] { //
        { 3, 4 }, { 5, 12 }, { 6, 8 }, { 7, 24 }, { 8, 15 }, { 9, 12 }, { 10, 24 }, //
        { 12, 16 }, { 15, 20 }, { 16, 30 }, { 18, 24 }, { 20, 21 } };
    for (Tensor vec : Tensors.matrixInt(array))
      checkVectorExact(vec);
  }

  @Test
  public void testComplex() {
    Scalar c1 = ComplexScalar.of(1, -5);
    Scalar c2 = ComplexScalar.of(2, 4);
    Scalar pair = Hypot.of(c1, c2);
    assertEquals(Sqrt.of(RealScalar.of(46)), pair);
    Scalar value = Hypot.ofVector(Tensors.of(c1, c2));
    assertEquals(value, pair);
    Scalar norm = Vector2Norm.of(Tensors.of(c1, c2));
    assertEquals(norm, pair);
  }

  @Test
  public void testNaNdivNaN() {
    Scalar s1 = DoubleScalar.INDETERMINATE;
    Scalar s2 = DoubleScalar.INDETERMINATE;
    Scalar s3 = s1.divide(s2);
    assertEquals(s3.toString(), "NaN");
  }

  @Test
  public void testInfNan() {
    Scalar s1 = DoubleScalar.POSITIVE_INFINITY;
    assertFalse(Scalars.isZero(s1));
    Scalar s2 = DoubleScalar.INDETERMINATE;
    assertFalse(Scalars.isZero(s2));
    try {
      Scalar s3 = Hypot.of(s1, s2); // NaN+NaN*I
      assertInstanceOf(ComplexScalar.class, s3);
      assertFalse(Scalars.isZero(s3));
      @SuppressWarnings("unused")
      Scalar s4 = ArcTan.FUNCTION.apply(s2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  @Test
  public void testFailScalar() {
    assertThrows(TensorRuntimeException.class, () -> Hypot.ofVector(RealScalar.ONE));
  }

  @Test
  public void testWithOne0() {
    Scalar scalar = Hypot.withOne(RealScalar.ZERO);
    assertEquals(scalar, RealScalar.ONE);
    ExactScalarQ.require(scalar);
  }

  @Test
  public void testWithOne1() {
    Tolerance.CHOP.requireClose( //
        Hypot.withOne(RealScalar.of(5)), //
        Sqrt.FUNCTION.apply(RealScalar.of(5 * 5 + 1)));
  }

  @Test
  public void testWithOne2() {
    Tolerance.CHOP.requireClose( //
        Hypot.withOne(RealScalar.of(0.5)), //
        Sqrt.FUNCTION.apply(RealScalar.of(0.25 + 1)));
  }

  @Test
  public void testQuantity() {
    Scalar qs1 = Quantity.of(3, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Scalar qs3 = Quantity.of(5, "m");
    assertEquals(Hypot.of(qs1, qs2), qs3);
  }

  @Test
  public void testQuantityZero() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(0, "m");
    assertEquals(Hypot.of(qs1, qs2), qs1);
  }

  @Test
  public void testQuantity2() {
    Scalar s1 = Quantity.of(-3, "m");
    Scalar s2 = Quantity.of(-4, "m");
    Scalar sum = s1.add(s2);
    assertEquals(sum, Quantity.of(-7, "m"));
    Scalar result = Sqrt.FUNCTION.apply(s1.multiply(s1).add(s2.multiply(s2)));
    assertEquals(result, Quantity.of(5, "m"));
    ExactScalarQ.require(result);
    Scalar hypot = Hypot.of(s1, s2);
    assertEquals(result, hypot);
    ExactScalarQ.require(hypot);
    assertEquals(Hypot.of(s1, s2), Hypot.of(s2, s1));
  }

  @Test
  public void testMixedUnitFail() {
    assertThrows(TensorRuntimeException.class, () -> Hypot.of(Quantity.of(2, "m"), Quantity.of(3, "s")));
  }
}
