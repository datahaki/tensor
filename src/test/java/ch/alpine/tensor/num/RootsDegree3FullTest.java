// code by jph
package ch.alpine.tensor.num;

import java.util.Map;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.red.Tally;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class RootsDegree3FullTest extends TestCase {
  private static void check(Tensor roots, Tensor depres) {
    Integers.requireEquals(roots.length(), depres.length());
    for (Tensor root : roots)
      assertTrue(depres.stream().anyMatch(s -> Chop._10.isClose(root, s)));
  }

  public void testCubic() {
    Tensor coeffs = Tensors.vector(2, 3, 4, 5);
    Tensor roots = Roots.of(coeffs);
    ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
    Tensor tensor = roots.map(scalarUnaryOperator);
    Chop._13.requireAllZero(tensor);
    Tensor depres = RootsDegree3.of(coeffs);
    check(roots, depres);
  }

  public void testMonomial() {
    Tensor coeffs = Tensors.vector(0, 0, 0, 10);
    Tensor roots = Roots.of(coeffs);
    ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
    Tensor tensor = roots.map(scalarUnaryOperator);
    assertEquals(tensor, Array.zeros(3));
    Tensor depres = RootsDegree3.of(coeffs);
    Chop._10.requireClose(depres, roots);
  }

  public void testMonomialShiftedP() {
    Tensor coeffs = Tensors.vector(1, 3, 3, 1);
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, Tensors.vector(-1, -1, -1));
    ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
    Tensor tensor = roots.map(scalarUnaryOperator);
    assertEquals(tensor, Array.zeros(3));
    Tensor depres = RootsDegree3.of(coeffs);
    Chop._10.requireClose(depres, roots);
  }

  public void testMonomialShiftedN() {
    Tensor coeffs = Tensors.vector(1, -3, 3, -1);
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, Tensors.vector(1, 1, 1));
    ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
    Tensor tensor = roots.map(scalarUnaryOperator);
    assertEquals(tensor, Array.zeros(3));
    Tensor depres = RootsDegree3.of(coeffs);
    Chop._10.requireClose(depres, roots);
  }

  public void testMonomialQuadShift() {
    Tensor coeffs = Tensors.vector(1, 1, -1, -1);
    Tensor roots = Roots.of(coeffs);
    Map<Tensor, Long> map = Tally.of(roots);
    assertEquals((long) map.get(RealScalar.ONE), 1);
    assertEquals((long) map.get(RealScalar.ONE.negate()), 2);
    ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
    Tensor tensor = roots.map(scalarUnaryOperator);
    Chop._07.requireAllZero(tensor);
    Tensor depres = RootsDegree3.of(coeffs);
    Chop._10.requireClose(depres, roots);
  }

  public void testCubicQuantity() {
    Tensor coeffs = Tensors.fromString("{2[m^-5], 3[m^-4], 4[m^-3], 5[m^-2]}");
    Tensor roots = Roots.of(coeffs);
    ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
    Tensor tensor = roots.map(scalarUnaryOperator);
    Chop._13.requireAllZero(tensor);
    Tensor depres = RootsDegree3.of(coeffs);
    check(roots, depres);
  }

  public void testCubicNumerics() {
    Tensor coeffs = Tensors.vector(0.7480756509468256, -0.11264914570345713, 0.5215628590156208, -0.8016542468533115);
    Tensor roots = Roots.of(coeffs);
    ScalarUnaryOperator scalarUnaryOperator = Polynomial.of(coeffs);
    Tensor tensor = roots.map(scalarUnaryOperator);
    Chop._05.requireAllZero(tensor);
    Tensor depres = RootsDegree3.of(coeffs);
    check(roots, depres);
  }

  public void testCubicChallenge() {
    Tensor coeffs = Tensors.vector(1.8850384838238452, -0.07845356111460325, -0.6128180724984655, -1.5845220466594934);
    Tensor roots = Roots.of(coeffs);
    Scalar m0 = Scalars.fromString("-0.6590816994450482 - 0.9180824258012533 * I");
    Scalar m1 = Scalars.fromString("-0.6590816994450482 + 0.9180824258012533 * I");
    Scalar m2 = RealScalar.of(0.9314107665802999);
    assertTrue(roots.stream().anyMatch(root -> Chop._05.isClose(root, m0)));
    assertTrue(roots.stream().anyMatch(root -> Chop._05.isClose(root, m1)));
    assertTrue(roots.stream().anyMatch(root -> Chop._05.isClose(root, m2)));
  }
  // {4.403491745360149, -3.6065243114260417, -4.588031155616683, -4.8648946627594114E-4}
}
