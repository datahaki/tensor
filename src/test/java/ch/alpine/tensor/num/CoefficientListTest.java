// code by jph
package ch.alpine.tensor.num;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

// TODO reinstantiate all tests
public class CoefficientListTest extends TestCase {
  public void testSimple() {
    Tensor zeros = Tensors.vector(3);
    Tensor coeffs = CoefficientList.of(zeros);
    Chop.NONE.requireZero(Polynomial.of(coeffs).apply(RealScalar.of(3)));
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, zeros);
  }

  public void testQuantityD1() {
    // Tensor zeros = Tensors.fromString("{3[m]}");
    // Tensor coeffs = CoefficientList.of(zeros);
    // Scalar result = Polynomial.of(coeffs).apply(Quantity.of(3, "m"));
    // ExactScalarQ.require(result);
    // assertEquals(result, Quantity.of(0, "m"));
    // Tensor roots = Roots.of(coeffs);
    // assertEquals(roots, zeros);
  }

  public void testQuantityD2() {
    // Tensor zeros = Tensors.fromString("{3[m], 4[m]}");
    // Tensor coeffs = CoefficientList.of(zeros);
    // Chop.NONE.requireZero(Polynomial.of(coeffs).apply(Quantity.of(3, "m")));
    // Chop.NONE.requireZero(Polynomial.of(coeffs).apply(Quantity.of(4, "m")));
    // Tensor roots = Roots.of(coeffs);
    // ExactTensorQ.require(roots);
    // assertEquals(roots, zeros);
  }

  public void testQuantityD3() {
    // Tensor zeros = Tensors.fromString("{3[m], 4[m], 6[m]}");
    // Tensor coeffs = CoefficientList.of(zeros);
    // assertEquals(Polynomial.of(coeffs).apply(Quantity.of(3, "m")), Quantity.of(0, "m^3"));
    // Chop._14.requireZero(Polynomial.of(coeffs).apply(Quantity.of(4, "m")));
    // Chop._14.requireZero(Polynomial.of(coeffs).apply(Quantity.of(6, "m")));
    // Tensor roots = Roots.of(coeffs);
    // Chop._14.requireClose(roots, zeros);
  }

  public void testEmptyFail() {
    AssertFail.of(() -> CoefficientList.of(Tensors.empty()));
  }
}
