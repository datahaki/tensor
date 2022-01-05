// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.ExactTensorQ;
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
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Mod;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PolynomialTest extends TestCase {
  public void testGauss() {
    Scalar scalar1 = Polynomial.of(Tensors.of( //
        GaussScalar.of(2, 7), GaussScalar.of(4, 7), GaussScalar.of(5, 7))) //
        .apply(GaussScalar.of(6, 7));
    Scalar scalar2 = Polynomial.of( //
        Tensors.vector(2, 4, 5)).apply(RealScalar.of(6));
    Scalar scalar3 = Mod.function(RealScalar.of(7)).apply(scalar2);
    assertEquals(scalar1.number().intValue(), scalar3.number().intValue());
  }

  public void testAccumulate() {
    Tensor coeffs = Tensors.vector(2, 1, -3, 2, 3, 0, 2);
    Tensor accumu = Accumulate.of(coeffs);
    assertEquals(Polynomial.of(coeffs).apply(RealScalar.ONE), Total.of(coeffs));
    assertEquals(Last.of(accumu), Total.of(coeffs));
    for (int index = 1; index < coeffs.length(); ++index) {
      Scalar scalar = Polynomial.of(coeffs.extract(index, coeffs.length())).apply(RealScalar.ONE);
      Scalar diff = (Scalar) Last.of(accumu).subtract(accumu.Get(index - 1));
      assertEquals(scalar, diff);
    }
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(-4, "m*s");
    Scalar qs2 = Quantity.of(3, "m");
    Scalar val = Quantity.of(2, "s");
    Scalar res = Polynomial.of(Tensors.of(qs1, qs2)).apply(val);
    assertEquals(res.toString(), "2[m*s]");
  }

  public void testQuaternionLinear() {
    Quaternion qs1 = Quaternion.of(1, 2, 3, 4);
    Quaternion qs2 = Quaternion.of(2, 5, -1, 0);
    assertTrue(RandomQuaternion.nonCommute(qs1, qs2));
    Scalar val = Quaternion.of(-1, 7, 6, -8);
    Tensor coeffs = Tensors.of(qs1, qs2);
    ScalarUnaryOperator series = Polynomial.of(coeffs);
    Scalar res = Polynomial.of(coeffs).apply(val);
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
      ScalarUnaryOperator series = Polynomial.of(coeffs);
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
    ScalarUnaryOperator series = Polynomial.of(coeffs);
    Scalar res = Polynomial.of(coeffs).apply(val);
    ExactScalarQ.require(res);
    Tensor roots = Roots.of(coeffs);
    roots.map(series); // non-zero
    // System.out.println();
  }

  public void testNullFail() {
    AssertFail.of(() -> Polynomial.of(null));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Polynomial.of(HilbertMatrix.of(3)));
  }

  public void testDerivativeSimple() {
    Tensor coeffs = Tensors.vector(-3, 4, -5, 8, 1);
    Tensor result = Polynomial.derivative_coeffs(coeffs);
    ExactTensorQ.require(result);
    assertEquals(result, Tensors.vector(4, -5 * 2, 8 * 3, 1 * 4));
  }

  public void testDerivativeEmpty() {
    assertEquals(Polynomial.derivative_coeffs(Tensors.vector()), Tensors.vector());
    assertEquals(Polynomial.derivative_coeffs(Tensors.vector(3)), Tensors.empty());
  }

  public void testDerLinEx() {
    Tensor coeffs = Tensors.fromString("{-13[bar], 0.27[K^-1*bar]}");
    ScalarUnaryOperator polynomial = Polynomial.of(coeffs);
    assertEquals(QuantityUnit.of(polynomial.apply(Quantity.of(3, "K"))), Unit.of("bar"));
    Tensor coeffs_d1 = Polynomial.derivative_coeffs(coeffs);
    assertEquals(coeffs_d1, Tensors.fromString("{0.27[K^-1*bar], 0.0[K^-2*bar]}"));
    ScalarUnaryOperator derivative = Polynomial.of(coeffs_d1);
    assertEquals(QuantityUnit.of(derivative.apply(Quantity.of(3, "K"))), Unit.of("bar*K^-1"));
    AssertFail.of(() -> derivative.apply(Quantity.of(3, "bar")));
  }

  public void testDerivativeLinear() {
    Tensor coeffs = Tensors.of(Quantity.of(3, "m"), Quantity.of(2, "m*s^-1"));
    Scalar position = Polynomial.of(coeffs).apply(Quantity.of(10, "s"));
    assertEquals(position, Quantity.of(23, "m"));
    Tensor coeffs_d1 = Polynomial.derivative_coeffs(coeffs);
    {
      assertEquals(coeffs_d1, Tensors.fromString("{2[m*s^-1], 0[m*s^-2]}"));
      ExactTensorQ.require(coeffs_d1);
      ScalarUnaryOperator d1 = Polynomial.of(coeffs_d1);
      AssertFail.of(() -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(Quantity.of(4, "s")), Quantity.of(2, "m*s^-1"));
    }
    Tensor coeffs_d2 = Polynomial.derivative_coeffs(coeffs_d1);
    {
      assertEquals(coeffs_d2, Tensors.fromString("{0[m*s^-2], 0[m*s^-3]}"));
      ExactTensorQ.require(coeffs_d2);
      ScalarUnaryOperator d2 = Polynomial.of(coeffs_d2);
      AssertFail.of(() -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(Quantity.of(4, "s")), Quantity.of(0, "m*s^-2"));
    }
  }

  public void testDerivativeGaussScalar() {
    GaussScalar a = GaussScalar.of(3, 17);
    GaussScalar b = GaussScalar.of(4, 17);
    GaussScalar x = GaussScalar.of(5, 17);
    Tensor coeffs = Tensors.of(a, b);
    Scalar position = Polynomial.of(coeffs).apply(x);
    assertEquals(position, GaussScalar.of(6, 17));
    Tensor coeffs_d1 = Polynomial.derivative_coeffs(coeffs);
    {
      assertEquals(coeffs_d1, Tensors.of(b, b.zero()));
      ExactTensorQ.require(coeffs_d1);
      ScalarUnaryOperator d1 = Polynomial.of(coeffs_d1);
      AssertFail.of(() -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(x), b);
    }
    Tensor coeffs_d2 = Polynomial.derivative_coeffs(coeffs_d1);
    {
      assertEquals(coeffs_d2, Tensors.of(b.zero(), b.zero()));
      ExactTensorQ.require(coeffs_d2);
      ScalarUnaryOperator d2 = Polynomial.of(coeffs_d2);
      AssertFail.of(() -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(x), b.zero());
    }
  }

  public void testDerivativeQuadr() {
    Tensor coeffs = Tensors.of(Quantity.of(3, "m"), Quantity.of(2, "m*s^-2"));
    Scalar position = Polynomial.of(coeffs).apply(Quantity.of(10, "s^2"));
    assertEquals(position, Quantity.of(23, "m"));
    Tensor coeffs_d1 = Polynomial.derivative_coeffs(coeffs);
    {
      assertEquals(coeffs_d1, Tensors.fromString("{2[m*s^-2], 0[m*s^-4]}"));
      ExactTensorQ.require(coeffs_d1);
      ScalarUnaryOperator d1 = Polynomial.of(coeffs_d1);
      AssertFail.of(() -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(Quantity.of(4, "s^2")), Quantity.of(2, "m*s^-2"));
    }
    Tensor coeffs_d2 = Polynomial.derivative_coeffs(coeffs_d1);
    {
      assertEquals(coeffs_d2, Tensors.fromString("{0[m*s^-4], 0[m*s^-6]}"));
      ExactTensorQ.require(coeffs_d2);
      ScalarUnaryOperator d2 = Polynomial.of(coeffs_d2);
      AssertFail.of(() -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(Quantity.of(4, "s^2")), Quantity.of(0, "m*s^-4"));
    }
  }

  public void testEmptyFail() {
    AssertFail.of(() -> Polynomial.of(Tensors.empty()));
  }

  public void testDerivativeScalarFail() {
    AssertFail.of(() -> Polynomial.derivative_coeffs(RealScalar.ONE));
  }

  public void testDerivativeMatrixFail() {
    AssertFail.of(() -> Polynomial.derivative_coeffs(HilbertMatrix.of(4, 5)));
  }

  public void testUnstructuredFail() {
    AssertFail.of(() -> Polynomial.derivative_coeffs(Tensors.fromString("{2, {1}}")));
  }
}
