// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RandomQuaternion;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.alg.Accumulate;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.jet.JetScalar;
import ch.alpine.tensor.lie.Quaternion;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Mod;

class PolynomialTest {
  @Test
  void testGauss() {
    Scalar scalar1 = Polynomial.of(Tensors.of( //
        GaussScalar.of(2, 7), GaussScalar.of(4, 7), GaussScalar.of(5, 7))) //
        .apply(GaussScalar.of(6, 7));
    Scalar scalar2 = Polynomial.of( //
        Tensors.vector(2, 4, 5)).apply(RealScalar.of(6));
    Scalar scalar3 = Mod.function(RealScalar.of(7)).apply(scalar2);
    assertEquals(scalar1.number().intValue(), scalar3.number().intValue());
  }

  @Test
  void testAccumulate() {
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

  @Test
  void testPlus() {
    Polynomial p1 = Polynomial.of(Tensors.vector(1, 5, 2, 10));
    assertEquals(p1, p1.gain(0));
    assertThrows(IllegalArgumentException.class, () -> p1.gain(-1));
    Polynomial p2 = Polynomial.of(Tensors.vector(3, 8));
    assertEquals(p1.degree(), 3);
    assertEquals(p2.degree(), 1);
    assertEquals(p1.plus(p2), p2.plus(p1));
    Polynomial p3 = Polynomial.of(Tensors.vector(0));
    assertEquals(p1.plus(p3), p3.plus(p1));
    assertEquals(p1.plus(p3), p1);
  }

  @Test
  void testDegree() {
    assertEquals(Polynomial.of(Tensors.vector(3, 0, 0)).degree(), 0);
    assertEquals(Polynomial.of(Tensors.vector(0, 0, 0)).degree(), -1);
  }

  @Test
  void testChop() {
    Polynomial polynomial = Polynomial.of(Tensors.vector(1, 0, 0, 1e-11)).chop(Chop._10);
    assertEquals(polynomial.coeffs(), UnitVector.of(2, 0));
  }

  @Test
  void testLastNonZero() {
    assertEquals(Polynomial.of(Tensors.vector(3, 2, 0, 6, 0, 0)).coeffs(), Tensors.vector(3, 2, 0, 6));
    assertEquals(Polynomial.of(Tensors.vector(3)).coeffs(), Tensors.vector(3));
    assertEquals(Polynomial.of(Tensors.vector(0)).coeffs(), Tensors.vector(0));
    assertEquals(Polynomial.of(Tensors.vector(1, 0)).coeffs(), Tensors.vector(1, 0));
    assertEquals(Polynomial.of(Tensors.vector(1, 0, 0)).coeffs(), Tensors.vector(1, 0));
  }

  @Test
  void testQuantity() {
    Scalar qs1 = Quantity.of(-4, "m*s");
    Scalar qs2 = Quantity.of(3, "m");
    Scalar val = Quantity.of(2, "s");
    Scalar res = Polynomial.of(Tensors.of(qs1, qs2)).apply(val);
    assertEquals(res.toString(), "2[m*s]");
  }

  @Test
  void testAccelerationConstant() {
    Scalar c0 = Quantity.of(3, "m*s^-1");
    Polynomial polynomial = Polynomial.of(Tensors.of(c0));
    Tensor roots = polynomial.roots();
    assertEquals(roots, Tensors.empty());
    Scalar t = Quantity.of(2, "kg");
    Scalar scalar = polynomial.apply(t);
    assertEquals(scalar, c0);
    assertThrows(Throw.class, () -> polynomial.apply(GaussScalar.of(2, 7)));
    Polynomial derivative = polynomial.derivative();
    derivative.apply(t);
  }

  /** Careful:
   * does not give the multiplicative neutral element!
   * 
   * @return polynomial x -> x where units of domain and values are identical as this polynomial */
  /* package */ static Polynomial series01(Tensor coeffs) {
    Polynomial polynomial = Polynomial.of(coeffs);
    Tensor c01 = coeffs.extract(0, 2);
    c01.set(Scalar::zero, 0);
    Scalar b = coeffs.Get(1);
    Unit unit = polynomial.getUnitValue().add(polynomial.getUnitDomain().negate());
    c01.set(Quantity.of(b.one(), unit), 1);
    return Polynomial.of(c01);
  }

  @Test
  void testAcceleration() {
    Scalar qs0 = Quantity.of(3, "m*s^-1");
    Scalar qs1 = Quantity.of(-4, "m*s^-2");
    Polynomial polynomial = Polynomial.of(Tensors.of(qs0, qs1));
    assertEquals(polynomial.coeffs(), polynomial.gain(0).coeffs());
    Tensor roots = polynomial.roots();
    assertEquals(roots, Tensors.fromString("{3/4[s]}"));
    assertEquals(polynomial.getUnitDomain(), Unit.of("s"));
    assertEquals(polynomial.derivative().getUnitDomain(), Unit.of("s"));
    assertEquals(polynomial.derivative().derivative().getUnitDomain(), Unit.of("s"));
    Scalar val = Quantity.of(2, "s");
    Scalar result = polynomial.apply(val);
    assertEquals(result, Scalars.fromString("-5[m*s^-1]"));
    assertEquals(polynomial.getUnitValue(), Unit.of("m*s^-1"));
    Polynomial integral = polynomial.antiderivative();
    assertEquals(integral.getUnitDomain(), Unit.of("s"));
    Scalar scalar = integral.apply(val);
    assertEquals(scalar, Quantity.of(-2, "m"));
    Polynomial identity = series01(polynomial.coeffs());
    assertEquals(identity.coeffs(), Tensors.fromString("{0[m*s^-1], 1[m*s^-2]}"));
    assertEquals(identity.getUnitDomain(), Unit.of("s"));
    assertEquals(identity.getUnitValue(), Unit.of("m*s^-1"));
    // assertEquals(polynomial.times(identity), polynomial);
  }

  @Test
  void testNeutral() {
    Scalar qs0 = Quantity.of(3, "m*s^-1");
    Scalar qs1 = Quantity.of(-4, "m*s^-2");
    Polynomial p1 = Polynomial.of(Tensors.of(qs0, qs1));
    Polynomial p2 = Polynomial.of(Tensors.of(Quantity.of(1, "m*s^-1"), qs1.zero()));
    p1.times(p2);
    // assertEquals(p1, p3);
  }

  @Test
  void testIntegralOne() {
    Scalar qs0 = Quantity.of(3, "m*s^-1");
    // Scalar val = Quantity.of(2, "s");
    Polynomial polynomial = Polynomial.of(Tensors.of(qs0));
    Polynomial integral = polynomial.antiderivative();
    // integral.apply(val);
    integral.coeffs();
    // System.out.println();
  }

  @Test
  void testMoment() {
    Scalar qs0 = Quantity.of(3, "m*s^-1");
    Scalar qs1 = Quantity.of(-4, "m*s^-2");
    // Scalar val = Quantity.of(2, "s");
    Polynomial polynomial = Polynomial.of(Tensors.of(qs0, qs1));
    assertEquals(polynomial, polynomial.gain(0));
    assertEquals(polynomial.getUnitDomain(), Unit.of("s"));
    assertEquals(polynomial.coeffs(), Tensors.fromString("{3[m*s^-1], -4[m*s^-2]}"));
    Polynomial shift1 = polynomial.gain(1);
    assertEquals(shift1.coeffs(), Tensors.fromString("{0[m], 3[m*s^-1], -4[m*s^-2]}"));
    assertEquals(shift1.getUnitDomain(), Unit.of("s"));
    assertEquals(shift1.getUnitValue(), Unit.of("m"));
    Polynomial shift2 = polynomial.gain(2);
    assertEquals(shift2.coeffs(), Tensors.fromString("{0[m*s], 0[m], 3[m*s^-1], -4[m*s^-2]}"));
    assertEquals(shift2.getUnitDomain(), Unit.of("s"));
    assertEquals(shift2.getUnitValue(), Unit.of("m*s"));
    for (int i = 0; i < 5; ++i) {
      Polynomial expect = polynomial.gain(i);
      Polynomial init = polynomial;
      for (int j = 0; j < i; ++j)
        init = init.gain(1);
      assertEquals(expect, init);
    }
  }

  @Test
  void testMomentCoeffs1() {
    Polynomial polynomial = Polynomial.of(Tensors.vector(3));
    Polynomial p2 = polynomial.gain(3);
    assertEquals(p2.coeffs(), Tensors.vector(0, 0, 0, 3));
  }

  @Test
  void testQuaternionLinear() {
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

  @Test
  void testQuaternionLinearMany() {
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

  @Test
  void testQuaternionQuadratic() {
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

  @Test
  void testNullFail() {
    assertThrows(NullPointerException.class, () -> Polynomial.of(null));
  }

  @Test
  void testMatrixFail() {
    assertThrows(Throw.class, () -> Polynomial.of(HilbertMatrix.of(3)));
  }

  @Test
  void testDerivativeSimple() {
    Polynomial coeffs = Polynomial.of(Tensors.vector(-3, 4, -5, 8, 1));
    Polynomial result = coeffs.derivative();
    ExactTensorQ.require(result.coeffs());
    assertEquals(result.coeffs(), Tensors.vector(4, -5 * 2, 8 * 3, 1 * 4));
  }

  @Test
  void testDerivativeEmpty() {
    assertEquals(Polynomial.of(Tensors.vector(3)).derivative().coeffs(), Tensors.vector(0));
  }

  @Test
  void testDerLinEx() {
    // Tensor coeffs = ;
    Polynomial polynomial = Polynomial.of(Tensors.fromString("{-13[bar], 0.27[K^-1*bar]}"));
    assertEquals(QuantityUnit.of(polynomial.apply(Quantity.of(3, "K"))), Unit.of("bar"));
    Polynomial derivative = polynomial.derivative();
    assertEquals(derivative.coeffs(), Tensors.fromString("{0.27[K^-1*bar], 0.0[K^-2*bar]}"));
    // ScalarUnaryOperator derivative = Polynomial.of(coeffs_d1);
    assertEquals(QuantityUnit.of(derivative.apply(Quantity.of(3, "K"))), Unit.of("bar*K^-1"));
    assertThrows(Throw.class, () -> derivative.apply(Quantity.of(3, "bar")));
  }

  @Test
  void testDerivativeLinear() {
    Polynomial polynomial = Polynomial.of(Tensors.of(Quantity.of(3, "m"), Quantity.of(2, "m*s^-1")));
    Scalar position = polynomial.apply(Quantity.of(10, "s"));
    assertEquals(position, Quantity.of(23, "m"));
    Polynomial d1 = polynomial.derivative();
    {
      assertEquals(d1.coeffs(), Tensors.fromString("{2[m*s^-1], 0[m*s^-2]}"));
      ExactTensorQ.require(d1.coeffs());
      assertThrows(Throw.class, () -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(Quantity.of(4, "s")), Quantity.of(2, "m*s^-1"));
    }
    Polynomial d2 = d1.derivative();
    {
      assertEquals(d2.coeffs(), Tensors.fromString("{0[m*s^-2], 0[m*s^-3]}"));
      ExactTensorQ.require(d2.coeffs());
      assertThrows(Throw.class, () -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(Quantity.of(4, "s")), Quantity.of(0, "m*s^-2"));
    }
  }

  @Test
  void testDerivativeGaussScalar() {
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
      assertThrows(Throw.class, () -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(x), b);
    }
    Polynomial d2 = d1.derivative();
    {
      assertEquals(d2.coeffs(), Tensors.of(b.zero(), b.zero()));
      ExactTensorQ.require(d2.coeffs());
      assertThrows(Throw.class, () -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(x), b.zero());
    }
  }

  @Test
  void testDerivativeQuadr() {
    Polynomial polynomial = Polynomial.of(Tensors.of(Quantity.of(3, "m"), Quantity.of(2, "m*s^-2")));
    Scalar position = polynomial.apply(Quantity.of(10, "s^2"));
    assertEquals(position, Quantity.of(23, "m"));
    Polynomial d1 = polynomial.derivative();
    {
      assertEquals(d1.coeffs(), Tensors.fromString("{2[m*s^-2], 0[m*s^-4]}"));
      ExactTensorQ.require(d1.coeffs());
      assertThrows(Throw.class, () -> d1.apply(Quantity.of(4, "A")));
      assertEquals(d1.apply(Quantity.of(4, "s^2")), Quantity.of(2, "m*s^-2"));
    }
    Polynomial d2 = d1.derivative();
    {
      assertEquals(d2.coeffs(), Tensors.fromString("{0[m*s^-4], 0[m*s^-6]}"));
      ExactTensorQ.require(d2.coeffs());
      assertThrows(Throw.class, () -> d2.apply(Quantity.of(4, "A")));
      assertEquals(d2.apply(Quantity.of(4, "s^2")), Quantity.of(0, "m*s^-4"));
    }
  }

  @Test
  void testIntegralCoeff() {
    Polynomial coeffs = Polynomial.of(Tensors.vector(2, 6, 3, 9, 0, 3));
    Polynomial integr = coeffs.antiderivative();
    Polynomial result = integr.derivative();
    assertEquals(coeffs, result);
  }

  @Test
  void testMultiplyCoeff() {
    Polynomial c1 = Polynomial.of(Tensors.vector(2, 6, 3, 9, 0, 3));
    assertEquals(c1.getUnitDomain(), Unit.ONE);
    assertEquals(c1.getUnitValue(), Unit.ONE);
    Polynomial c2 = Polynomial.of(Tensors.vector(5, 7, 1));
    assertEquals(c2.toString(), "Polynomial[{5, 7, 1}]");
    Tensor roots = c2.roots();
    Tolerance.CHOP.requireAllZero(roots.map(c2));
    assertFalse(c1.equals(c2));
    assertFalse(c1.equals(null));
    assertFalse(c1.equals((Object) Pi.VALUE));
    assertFalse(c1.hashCode() == c2.hashCode());
    Polynomial pd = c1.times(c2);
    Polynomial al = c2.times(c1);
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

  @Test
  void testMultiplyCoeffUnits() {
    Polynomial c1 = Polynomial.of(Tensors.fromString("{1[m^-1],3[m^-2]}"));
    Polynomial c2 = Polynomial.of(Tensors.fromString("{2[m^-1],3[m^-2],-3[m^-3]}"));
    assertEquals(c2.getUnitDomain(), Unit.of("m"));
    assertEquals(c2.getUnitValue(), Unit.of("m^-1"));
    Polynomial pd = c1.times(c2);
    {
      Scalar x = Quantity.of(RationalScalar.HALF, "m");
      Scalar t1 = c1.apply(x).multiply(c2.apply(x));
      Scalar t2 = pd.apply(x);
      assertEquals(t1, t2);
    }
    Tensor coeffs = c2.coeffs();
    coeffs.set(Scalar::zero, 0);
    assertEquals(c2.derivative().antiderivative().coeffs(), coeffs);
  }

  @Test
  void testGain() {
    Scalar qs1 = Quantity.of(-4, "m*s");
    Scalar qs2 = Quantity.of(3, "m");
    Polynomial polynomial = Polynomial.of(Tensors.of(qs1, qs2));
    assertEquals(polynomial.gain(3), polynomial.gain(1).gain(1).gain(1));
    Unit unit1 = polynomial.getUnitDomain();
    Unit unit2 = polynomial.gain(3).getUnitDomain();
    assertEquals(unit1, unit2);
    assertEquals(unit1, Unit.of("s"));
    Scalar scalar = polynomial.moment(2, Quantity.of(1, "s"), Quantity.of(2, "s"));
    assertEquals(scalar, Scalars.fromString("23/12[m*s^4]"));
  }

  @Test
  void testEquals() {
    assertNotEquals(Polynomial.of(Tensors.vector(1, 2, 3)), "abc");
  }

  @Test
  void testDegree0() {
    Tensor ydata = Tensors.vector(5, -2);
    Polynomial polynomial = Polynomial.fit(Tensors.vector(10, 11), ydata, 0);
    assertEquals(polynomial.coeffs().toString(), "{3/2}");
    assertEquals(Mean.of(ydata), RationalScalar.of(3, 2));
  }

  @Test
  void testDegree1() throws ClassNotFoundException, IOException {
    Polynomial polynomial = Serialization.copy(Polynomial.fit(Tensors.vector(10, 11), Tensors.vector(5, -2), 1));
    ExactTensorQ.require(polynomial.coeffs());
    assertEquals(polynomial.coeffs().toString(), "{75, -7}");
    Tensor roots = polynomial.roots();
    assertEquals(roots.toString(), "{75/7}");
    // ScalarUnaryOperator series = Polynomial.of(coeffs);
    assertEquals(polynomial.apply(RealScalar.of(10)), RealScalar.of(5));
    assertEquals(polynomial.apply(RealScalar.of(11)), RealScalar.of(-2));
  }

  @Test
  void testQuaternionDeg1() {
    Tensor xdata = Tensors.of(RandomQuaternion.get(), RandomQuaternion.get());
    Tensor ydata = Tensors.of(RandomQuaternion.get(), RandomQuaternion.get());
    Polynomial polynomial = Polynomial.fit(xdata, ydata, 1);
    ExactTensorQ.require(polynomial.coeffs());
    // ScalarUnaryOperator series = Polynomial.of(coeffs);
    for (int index = 0; index < xdata.length(); ++index)
      assertEquals(polynomial.apply(xdata.Get(index)), ydata.Get(index));
  }

  @Test
  void testMixedUnits() {
    ScalarUnaryOperator pascal = QuantityMagnitude.SI().in("Pa");
    ScalarUnaryOperator kelvin = QuantityMagnitude.SI().in("K");
    for (int degree = 0; degree <= 4; ++degree) {
      Tensor x = Tensors.fromString("{100[K], 110.0[K], 120[K], 133[K], 140[K], 150[K]}");
      Tensor y = Tensors.fromString("{10[bar], 20[bar], 22[bar], 23[bar], 25[bar], 26.0[bar]}");
      Polynomial f0 = Polynomial.fit(x, y, degree);
      if (1 == f0.coeffs().length()) {
        Polynomial f1 = f0.derivative();
        assertEquals(f0.coeffs().length(), f1.coeffs().length());
      } else //
      if (1 < f0.coeffs().length()) {
        Polynomial f1 = f0.derivative();
        if (f0.coeffs().length() != 2)
          assertEquals(f0.coeffs().length(), f1.coeffs().length() + 1);
      }
      ScalarUnaryOperator x_to_y = Polynomial.fit(x, y, degree);
      Scalar pressure = x_to_y.apply(Quantity.of(103, "K"));
      pascal.apply(pressure);
      ScalarUnaryOperator y_to_x = Polynomial.fit(y, x, degree);
      Scalar temperat = y_to_x.apply(Quantity.of(15, "bar"));
      kelvin.apply(temperat);
    }
  }

  @Test
  void testLinear() {
    Random random = new Random(2);
    Polynomial polynomial = Polynomial.of(Tensors.vector(-2., 3., 0.5));
    Distribution distribution = TriangularDistribution.of(-2, 1, 2);
    for (int n = 5; n < 10; ++n) {
      Tensor xdata = RandomVariate.of(distribution, random, n);
      Tensor ydata = xdata.map(polynomial);
      Polynomial fit = Polynomial.fit(xdata, ydata, 4);
      assertEquals(fit.degree(), polynomial.degree());
    }
  }

  @Test
  void testDegreeLargeFail() {
    assertThrows(IllegalArgumentException.class, () -> Polynomial.fit(Tensors.vector(10, 11), Tensors.vector(5, -2), 2));
  }

  @Test
  void testNegativeFail() {
    assertThrows(IllegalArgumentException.class, () -> Polynomial.fit(Tensors.vector(10, 11), Tensors.vector(5, -2), -1));
  }

  @Test
  void testEmptyFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> Polynomial.of(Tensors.empty()));
  }

  @Test
  void testDerivativeScalarFail() {
    assertThrows(Throw.class, () -> Polynomial.of(RealScalar.ONE));
  }

  @Test
  void testDerivativeMatrixFail() {
    assertThrows(Throw.class, () -> Polynomial.of(HilbertMatrix.of(4, 5)));
  }

  @Test
  void testUnstructuredFail() {
    assertThrows(Throw.class, () -> Polynomial.of(Tensors.fromString("{2, {1}}")));
  }
}
