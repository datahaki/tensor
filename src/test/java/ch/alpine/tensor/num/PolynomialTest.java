// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RandomQuaternion;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.jet.JetScalar;
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

  public void testAccelerationConstant() {
    Scalar c0 = Quantity.of(3, "m*s^-1");
    Polynomial polynomial = Polynomial.of(Tensors.of(c0));
    Scalar t = Quantity.of(2, "kg");
    Scalar scalar = polynomial.apply(t);
    assertEquals(scalar, c0);
    AssertFail.of(() -> polynomial.apply(GaussScalar.of(2, 7)));
    Polynomial derivative = polynomial.derivative();
    derivative.apply(t);
  }

  public void testAcceleration() {
    Scalar qs0 = Quantity.of(3, "m*s^-1");
    Scalar qs1 = Quantity.of(-4, "m*s^-2");
    Scalar val = Quantity.of(2, "s");
    Polynomial polynomial = Polynomial.of(Tensors.of(qs0, qs1));
    Scalar res = polynomial.apply(val);
    Polynomial integral = polynomial.integral();
    Scalar scalar = integral.apply(val);
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
  }

  public void testNullFail() {
    AssertFail.of(() -> Polynomial.of(null));
  }

  public void testMatrixFail() {
    AssertFail.of(() -> Polynomial.of(HilbertMatrix.of(3)));
  }

  public void testDerivativeSimple() {
    Polynomial coeffs = Polynomial.of(Tensors.vector(-3, 4, -5, 8, 1));
    Polynomial result = coeffs.derivative();
    ExactTensorQ.require(result.coeffs());
    assertEquals(result.coeffs(), Tensors.vector(4, -5 * 2, 8 * 3, 1 * 4));
  }

  public void testDerivativeEmpty() {
    assertEquals(Polynomial.of(Tensors.vector(3)).derivative().coeffs(), Tensors.vector(0));
  }

  public void testDerLinEx() {
    // Tensor coeffs = ;
    Polynomial polynomial = Polynomial.of(Tensors.fromString("{-13[bar], 0.27[K^-1*bar]}"));
    assertEquals(QuantityUnit.of(polynomial.apply(Quantity.of(3, "K"))), Unit.of("bar"));
    Polynomial derivative = polynomial.derivative();
    assertEquals(derivative.coeffs(), Tensors.fromString("{0.27[K^-1*bar], 0.0[K^-2*bar]}"));
    // ScalarUnaryOperator derivative = Polynomial.of(coeffs_d1);
    assertEquals(QuantityUnit.of(derivative.apply(Quantity.of(3, "K"))), Unit.of("bar*K^-1"));
    AssertFail.of(() -> derivative.apply(Quantity.of(3, "bar")));
  }

  public void testDerivativeLinear() {
    Polynomial polynomial = Polynomial.of(Tensors.of(Quantity.of(3, "m"), Quantity.of(2, "m*s^-1")));
    Scalar position = polynomial.apply(Quantity.of(10, "s"));
    assertEquals(position, Quantity.of(23, "m"));
    Polynomial d1 = polynomial.derivative();
    {
      assertEquals(d1.coeffs(), Tensors.fromString("{2[m*s^-1], 0[m*s^-2]}"));
      ExactTensorQ.require(d1.coeffs());
      AssertFail.of(() -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(Quantity.of(4, "s")), Quantity.of(2, "m*s^-1"));
    }
    Polynomial d2 = d1.derivative();
    {
      assertEquals(d2.coeffs(), Tensors.fromString("{0[m*s^-2], 0[m*s^-3]}"));
      ExactTensorQ.require(d2.coeffs());
      AssertFail.of(() -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(Quantity.of(4, "s")), Quantity.of(0, "m*s^-2"));
    }
  }

  public void testDerivativeGaussScalar() {
    GaussScalar a = GaussScalar.of(3, 17);
    GaussScalar b = GaussScalar.of(4, 17);
    GaussScalar x = GaussScalar.of(5, 17);
    Polynomial polynomial = Polynomial.of(Tensors.of(a, b));
    Scalar position = polynomial.apply(x);
    assertEquals(position, GaussScalar.of(6, 17));
    Polynomial d1 = polynomial.derivative();
    {
      assertEquals(d1.coeffs(), Tensors.of(b, b.zero()));
      ExactTensorQ.require(d1.coeffs());
      AssertFail.of(() -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(x), b);
    }
    Polynomial d2 = d1.derivative();
    {
      assertEquals(d2.coeffs(), Tensors.of(b.zero(), b.zero()));
      ExactTensorQ.require(d2.coeffs());
      AssertFail.of(() -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(x), b.zero());
    }
  }

  public void testDerivativeQuadr() {
    Polynomial polynomial = Polynomial.of(Tensors.of(Quantity.of(3, "m"), Quantity.of(2, "m*s^-2")));
    Scalar position = polynomial.apply(Quantity.of(10, "s^2"));
    assertEquals(position, Quantity.of(23, "m"));
    Polynomial d1 = polynomial.derivative();
    {
      assertEquals(d1.coeffs(), Tensors.fromString("{2[m*s^-2], 0[m*s^-4]}"));
      ExactTensorQ.require(d1.coeffs());
      AssertFail.of(() -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(Quantity.of(4, "s^2")), Quantity.of(2, "m*s^-2"));
    }
    Polynomial d2 = d1.derivative();
    {
      assertEquals(d2.coeffs(), Tensors.fromString("{0[m*s^-4], 0[m*s^-6]}"));
      ExactTensorQ.require(d2.coeffs());
      AssertFail.of(() -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(Quantity.of(4, "s^2")), Quantity.of(0, "m*s^-4"));
    }
  }

  public void testIntegralCoeff() {
    Polynomial coeffs = Polynomial.of(Tensors.vector(2, 6, 3, 9, 0, 3));
    Polynomial integr = coeffs.integral();
    Polynomial result = integr.derivative();
    assertEquals(coeffs, result);
  }

  public void testMultiplyCoeff() {
    Polynomial c1 = Polynomial.of(Tensors.vector(2, 6, 3, 9, 0, 3));
    Polynomial c2 = Polynomial.of(Tensors.vector(5, 7, 1));
    assertEquals(c2.toString(), "Polynomial[{5, 7, 1}]");
    Tensor roots = c2.roots();
    Tolerance.CHOP.requireAllZero(roots.map(c2));
    assertFalse(c1.equals(c2));
    assertFalse(c1.equals(null));
    assertFalse(c1.equals((Object) Pi.VALUE));
    assertFalse(c1.hashCode() == c2.hashCode());
    Polynomial pd = c1.product(c2);
    Polynomial al = c2.product(c1);
    assertEquals(pd.coeffs(), al.coeffs());
    assertEquals(pd.coeffs(), Tensors.vector(10, 44, 59, 72, 66, 24, 21, 3));
    {
      Scalar x = RationalScalar.HALF;
      Scalar t1 = c1.apply(x).multiply(c2.apply(x));
      Scalar t2 = pd.apply(x);
      assertEquals(t1, t2);
    }
    {
      JetScalar x = JetScalar.of(RationalScalar.HALF, 3);
      Scalar t1 = c1.apply(x).multiply(c2.apply(x));
      Scalar t2 = pd.apply(x);
      assertEquals(t1, t2);
    }
  }

  public void testMultiplyCoeffUnits() {
    Polynomial c1 = Polynomial.of(Tensors.fromString("{1[m^-1],3[m^-2]}"));
    Polynomial c2 = Polynomial.of(Tensors.fromString("{2[m^-1],3[m^-2],-3[m^-3]}"));
    Polynomial pd = c1.product(c2);
    {
      Scalar x = Quantity.of(RationalScalar.HALF, "m");
      Scalar t1 = c1.apply(x).multiply(c2.apply(x));
      Scalar t2 = pd.apply(x);
      assertEquals(t1, t2);
    }
    Tensor coeffs = c2.coeffs();
    coeffs.set(Scalar::zero, 0);
    assertEquals(c2.derivative().integral().coeffs(), coeffs);
  }

  public void testEmptyFail() {
    AssertFail.of(() -> Polynomial.of(Tensors.empty()));
  }

  public void testDerivativeScalarFail() {
    AssertFail.of(() -> Polynomial.of(RealScalar.ONE));
  }

  public void testDerivativeMatrixFail() {
    AssertFail.of(() -> Polynomial.of(HilbertMatrix.of(4, 5)));
  }

  public void testUnstructuredFail() {
    AssertFail.of(() -> Polynomial.of(Tensors.fromString("{2, {1}}")));
  }
}
