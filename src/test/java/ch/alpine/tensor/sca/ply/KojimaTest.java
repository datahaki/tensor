// code by gemini
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.sca.Abs;
import ch.alpine.tensor.sca.Sign;

class KojimaTest {
  private static Scalar maxActualRootModulus(Tensor coeffs) {
    return Roots.of(coeffs).stream().map(Scalar.class::cast).map(Abs.FUNCTION).reduce(Max::of).orElse(RealScalar.ZERO);
  }

  private static void assertValidUpperBound(RootsBounds bound, Tensor coeffs) {
    Scalar estimated = bound.of(coeffs);
    Scalar actualMax = maxActualRootModulus(coeffs);
    assertTrue(Sign.isPositiveOrZero(estimated.subtract(actualMax)), () -> bound + " returned " + estimated + " < actual max root modulus " + actualMax);
  }

  @Test
  void testDegreeOneLinear() {
    // 3 + 2x = 0 -> root at x = -1.5, modulus = 1.5
    Tensor linear = Tensors.vector(3, 2);
    assertEquals(Rational.of(3, 2), RootsBounds.KOJIMA.of(linear));
    assertValidUpperBound(RootsBounds.KOJIMA, linear);
    assertValidUpperBound(RootsBounds.FUJIWARA, linear);
  }

  @Test
  void testQuadratic() {
    // x^2 + 10x + 100 = 0 -> roots -5 +- 5*sqrt(3)*i, modulus = 10
    Tensor quadratic = Tensors.vector(100, 10, 1);
    assertEquals(RealScalar.of(20), RootsBounds.KOJIMA.of(quadratic));
    assertEquals(RealScalar.of(20), RootsBounds.FUJIWARA.of(quadratic));
    assertValidUpperBound(RootsBounds.KOJIMA, quadratic);
    assertValidUpperBound(RootsBounds.FUJIWARA, quadratic);
  }

  @Test
  void testKojimaBeatsLagrange2AndCauchyOnGradedCoefficients() {
    // x^3 + 6x^2 + 36x + 250 = 0
    Tensor graded = Tensors.vector(250, 36, 6, 1);
    Scalar kojima = RootsBounds.KOJIMA.of(graded);
    Scalar lagrange2 = RootsBounds.LAGRANGE2.of(graded);
    Scalar cauchy = RootsBounds.CAUCHY.of(graded);
    assertEquals(RealScalar.of(12), kojima);
    assertTrue(Scalars.lessThan(kojima, lagrange2));
    assertTrue(Scalars.lessThan(kojima, cauchy));
    assertValidUpperBound(RootsBounds.KOJIMA, graded);
    assertValidUpperBound(RootsBounds.SUN_HSIEH2, graded);
  }

  @Test
  void testZeroIntermediateCoefficientsFallback() {
    // x^3 + 100x + 1000 = 0 (c_2 == 0, max root modulus ~ 12.106)
    Tensor missingQuadratic = Tensors.vector(1000, 100, 0, 1);
    assertValidUpperBound(RootsBounds.KOJIMA, missingQuadratic);
    assertValidUpperBound(RootsBounds.FUJIWARA, missingQuadratic);
    // x^2 + 100 = 0 (c_1 == 0, roots +-10i)
    Tensor depressedQuadratic = Tensors.vector(100, 0, 1);
    assertValidUpperBound(RootsBounds.KOJIMA, depressedQuadratic);
    assertValidUpperBound(RootsBounds.FUJIWARA, depressedQuadratic);
  }

  @Test
  void testKojimaBeatsCauchyAndLagrange1OnGradedCoefficients() {
    // x^3 + 6x^2 + 36x + 250 = 0
    Tensor graded = Tensors.vector(250, 36, 6, 1);
    Scalar kojima = RootsBounds.KOJIMA.of(graded);
    Scalar cauchy = RootsBounds.CAUCHY.of(graded);
    Scalar lagrange1 = RootsBounds.LAGRANGE1.of(graded);
    assertEquals(RealScalar.of(12), kojima);
    assertEquals(RealScalar.of(251), cauchy);
    assertEquals(RealScalar.of(292), lagrange1);
    assertTrue(Scalars.lessThan(kojima, cauchy));
    assertTrue(Scalars.lessThan(kojima, lagrange1));
    for (RootsBounds bound : RootsBounds.values()) {
      assertValidUpperBound(bound, graded);
    }
  }

  @Test
  void testTrailingZeroRoots() {
    // x^3 + 4x^2 = x^2(x + 4) = 0 -> roots 0, 0, -4
    Tensor trailingZeros = Tensors.vector(0, 0, 4, 1);
    assertValidUpperBound(RootsBounds.KOJIMA, trailingZeros);
    assertValidUpperBound(RootsBounds.FUJIWARA, trailingZeros);
  }

  @Test
  void testQuantityCoefficientsWithMixedUnits() {
    // Polynomial in x [s^-1]: (x + 2[s^-1])(x + 3[s^-1]) = 6[s^-2] + 5[s^-1]*x + 1*x^2
    Tensor withUnits = Tensors.of(Quantity.of(6, "s^-2"), Quantity.of(5, "s^-1"), RealScalar.ONE);
    Scalar kojima = RootsBounds.KOJIMA.of(withUnits);
    RootsBounds.FUJIWARA.of(withUnits);
    assertEquals(Quantity.of(10, "s^-1"), kojima);
    assertValidUpperBound(RootsBounds.KOJIMA, withUnits);
    assertValidUpperBound(RootsBounds.FUJIWARA, withUnits);
  }

  @Test
  void testQuantityWithZeroIntermediateCoefficient() {
    // x^2 + 9[s^-2] = 0 -> roots +-3i [s^-1]
    Tensor depressedUnits = Tensors.of(Quantity.of(9, "s^-2"), Quantity.of(0, "s^-1"), RealScalar.ONE);
    assertValidUpperBound(RootsBounds.KOJIMA, depressedUnits);
    assertValidUpperBound(RootsBounds.FUJIWARA, depressedUnits);
  }

  @Test
  void testEmptyThrows() {
    assertThrows(Exception.class, () -> RootsBounds.KOJIMA.of(Tensors.empty()));
    assertThrows(Exception.class, () -> RootsBounds.FUJIWARA.of(Tensors.empty()));
  }
}
