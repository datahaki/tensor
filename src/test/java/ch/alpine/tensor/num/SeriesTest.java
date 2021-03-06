// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RandomQuaternion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Mod;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class SeriesTest extends TestCase {
  public void testEmptyReal() {
    Scalar scalar = Series.of(Tensors.empty()).apply(RealScalar.of(2));
    assertEquals(scalar, RealScalar.ZERO);
    ExactScalarQ.require(scalar);
  }

  public void testEmptyGaussian() {
    Scalar scalar = Series.of(Tensors.empty()).apply(GaussScalar.of(4, 7));
    assertEquals(scalar, GaussScalar.of(0, 7));
    ExactScalarQ.require(scalar);
  }

  public void testGauss() {
    Scalar scalar1 = Series.of(Tensors.of( //
        GaussScalar.of(2, 7), GaussScalar.of(4, 7), GaussScalar.of(5, 7))) //
        .apply(GaussScalar.of(6, 7));
    Scalar scalar2 = Series.of( //
        Tensors.vector(2, 4, 5)).apply(RealScalar.of(6));
    Scalar scalar3 = Mod.function(RealScalar.of(7)).apply(scalar2);
    assertEquals(scalar1.number().intValue(), scalar3.number().intValue());
  }

  public void testAccumulate() {
    Tensor coeffs = Tensors.vector(2, 1, -3, 2, 3, 0, 2);
    Tensor accumu = Accumulate.of(coeffs);
    assertEquals(Series.of(coeffs).apply(RealScalar.ONE), Total.of(coeffs));
    assertEquals(Last.of(accumu), Total.of(coeffs));
    for (int index = 1; index < coeffs.length(); ++index) {
      Scalar scalar = Series.of(coeffs.extract(index, coeffs.length())).apply(RealScalar.ONE);
      Scalar diff = (Scalar) Last.of(accumu).subtract(accumu.Get(index - 1));
      assertEquals(scalar, diff);
    }
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(-4, "m*s");
    Scalar qs2 = Quantity.of(3, "m");
    Scalar val = Quantity.of(2, "s");
    Scalar res = Series.of(Tensors.of(qs1, qs2)).apply(val);
    assertEquals(res.toString(), "2[m*s]");
  }

  public void testQuaternionLinear() {
    Quaternion qs1 = Quaternion.of(1, 2, 3, 4);
    Quaternion qs2 = Quaternion.of(2, 5, -1, 0);
    assertTrue(RandomQuaternion.nonCommute(qs1, qs2));
    Scalar val = Quaternion.of(-1, 7, 6, -8);
    Tensor coeffs = Tensors.of(qs1, qs2);
    ScalarUnaryOperator series = Series.of(coeffs);
    Scalar res = Series.of(coeffs).apply(val);
    ExactScalarQ.require(res);
    Tensor roots = Roots.of(coeffs);
    Scalar result = series.apply(roots.Get(0));
    Tolerance.CHOP.requireZero(result);
  }

  public void testQuaternionLinearMany() {
    for (int index = 0; index < 10; ++index) {
      Scalar qs1 = RandomQuaternion.get();
      Scalar qs2 = RandomQuaternion.get();
      Tensor coeffs = Tensors.of(qs1, qs2);
      ScalarUnaryOperator series = Series.of(coeffs);
      Tensor roots = Roots.of(coeffs);
      assertEquals(roots.length(), 1);
      Scalar result = series.apply(roots.Get(0));
      Chop.NONE.requireZero(result);
    }
  }

  public void testQuaternionQuadratic() {
    Scalar qs1 = Quaternion.of(1, 2, 3, 4);
    Scalar qs2 = Quaternion.of(2, 5, -1, 0);
    Scalar qs3 = Quaternion.of(-3, 7, 10, -11);
    assertFalse(qs1.multiply(qs2).equals(qs2.multiply(qs1)));
    Scalar val = Quaternion.of(-1, 7, 6, -8);
    Tensor coeffs = Tensors.of(qs1, qs2, qs3);
    ScalarUnaryOperator series = Series.of(coeffs);
    Scalar res = Series.of(coeffs).apply(val);
    ExactScalarQ.require(res);
    Tensor roots = Roots.of(coeffs);
    roots.map(series); // non-zero
    // System.out.println();
  }

  public void testEmpty() {
    assertEquals(Series.of(Tensors.empty()).apply(Pi.VALUE), RealScalar.ZERO);
  }

  public void testNullFail() {
    AssertFail.of(() -> Series.of(null));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Series.of(HilbertMatrix.of(3)));
  }
}
