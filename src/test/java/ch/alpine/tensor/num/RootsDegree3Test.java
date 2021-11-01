// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Imag;
import junit.framework.TestCase;

public class RootsDegree3Test extends TestCase {
  public void testSteer() {
    Scalar c = RealScalar.of(+0.8284521034333863);
    Scalar a = RealScalar.of(-0.33633373640449604);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, c, RealScalar.ZERO, a);
    ScalarUnaryOperator cubic = Polynomial.of(coeffs);
    Tensor roots = Roots.of(Tensors.of(RealScalar.ZERO, c, RealScalar.ZERO, a));
    Chop.NONE.requireAllZero(Imag.of(roots));
    Chop._13.requireAllZero(roots.get(1));
    Chop._13.requireAllZero(roots.map(cubic));
  }

  public void testCubicChallenge2() {
    Tensor coeffs = Tensors.vector(1.5583019232667707, 0.08338030361650195, 0.5438230916311243, 1.1822223716596811);
    ScalarUnaryOperator polynomial = Polynomial.of(coeffs);
    // roots obtained by Mathematica:
    Scalar cR = RealScalar.of(-1.2487729899770943);
    Chop._12.requireZero(polynomial.apply(cR));
    Scalar cP = ComplexScalar.of(0.3943861563603456, +0.9486756887529066);
    Scalar cN = ComplexScalar.of(0.3943861563603456, -0.9486756887529066);
    Chop._12.requireZero(polynomial.apply(cP));
    Chop._12.requireZero(polynomial.apply(cN));
    Tensor roots = Roots.of(coeffs);
    assertTrue(roots.stream().anyMatch(root -> Chop._12.isClose(root, cR)));
    assertTrue(roots.stream().anyMatch(root -> Chop._12.isClose(root, cP)));
    assertTrue(roots.stream().anyMatch(root -> Chop._12.isClose(root, cN)));
    Tensor rootz = RootsDegree3Full.of(coeffs);
    assertTrue(rootz.stream().anyMatch(root -> Chop._07.isClose(root, cR)));
    assertTrue(rootz.stream().anyMatch(root -> Chop._07.isClose(root, cP)));
    assertTrue(rootz.stream().anyMatch(root -> Chop._07.isClose(root, cN)));
  }

  public void testOrdering1() {
    Tensor coeffs = Tensors.vector(4, 5, 0, 1);
    Tensor actual = Roots.of(coeffs);
    Tensor expect = Tensors.fromString("{-0.72407555138628, 0.36203777569313966-2.322329445424682*I, 0.36203777569314005+2.322329445424681*I}");
    Tolerance.CHOP.requireClose(actual, expect);
  }

  public void testOrdering2() {
    Tensor coeffs = Tensors.vector(-4, 5, 0, 1);
    Tensor actual = Roots.of(coeffs);
    Tensor expect = Tensors.fromString("{-0.3620377756931403-2.3223294454246814*I, -0.3620377756931403+2.3223294454246814*I, 0.7240755513862799}");
    Tolerance.CHOP.requireClose(actual, expect);
  }

  public void testOrdering3() {
    Tensor coeffs = Tensors.vector(-4, -5, 0, 1);
    Tensor actual = Roots.of(coeffs);
    Tensor expect = Tensors.fromString("{-1.5615528128088303, -1, 2.56155281280883}");
    Tolerance.CHOP.requireClose(actual, expect);
  }

  public void testRoots3() {
    Tensor roots = Tensors.vector(0.27349919995262256, 0.28215588800565544, 0.3056009752969802);
    Tensor coeffs = CoefficientList.of(roots);
    Chop._09.requireClose(roots, Sort.of(RootsDegree3Full.of(coeffs)));
    Chop._12.requireClose(roots, Sort.of(RootsDegree3.of(coeffs)));
  }

  public void testTriple1() {
    // {0.22765732048577852, 0.22765732048577852, 0.22765732048577852}
    Tensor roots = Tensors.vector(2.146361758590232, 2.146361758590232, 2.146361758590232);
    Tensor coeffs = CoefficientList.of(roots);
    Tensor r1 = RootsDegree3Full.of(coeffs);
    Tensor r2 = RootsDegree3.of(coeffs);
    Chop._12.requireClose(roots, r1);
    Chop._12.requireClose(roots, r2);
  }

  public void testTriple2() {
    Tensor roots = Tensors.vector(0.22765732048577852, 0.22765732048577852, 0.22765732048577852);
    Tensor coeffs = CoefficientList.of(roots);
    Tensor r1 = RootsDegree3Full.of(coeffs);
    Tensor r2 = RootsDegree3.of(coeffs);
    Chop._12.requireClose(roots, r1);
    Chop._12.requireClose(roots, r2);
  }
}
