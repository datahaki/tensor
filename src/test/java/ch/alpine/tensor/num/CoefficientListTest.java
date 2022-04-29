// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.ExactScalarQ;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

public class CoefficientListTest {
  @Test
  public void testSimple() {
    Tensor zeros = Tensors.vector(3);
    Tensor coeffs = CoefficientList.of(zeros);
    Chop.NONE.requireZero(Polynomial.of(coeffs).apply(RealScalar.of(3)));
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, zeros);
  }

  @Test
  public void testQuantityD1() {
    Tensor zeros = Tensors.fromString("{3[m]}");
    Tensor coeffs = CoefficientList.of(zeros);
    Scalar result = Polynomial.of(coeffs).apply(Quantity.of(3, "m"));
    ExactScalarQ.require(result);
    assertEquals(result, Quantity.of(0, "m"));
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, zeros);
  }

  @Test
  public void testQuantityD2() {
    Tensor zeros = Tensors.fromString("{3[m], 4[m]}");
    Tensor coeffs = CoefficientList.of(zeros);
    Chop.NONE.requireZero(Polynomial.of(coeffs).apply(Quantity.of(3, "m")));
    Chop.NONE.requireZero(Polynomial.of(coeffs).apply(Quantity.of(4, "m")));
    Tensor roots = Roots.of(coeffs);
    ExactTensorQ.require(roots);
    assertEquals(roots, zeros);
  }

  @Test
  public void testQuantityD3() {
    Tensor zeros = Tensors.fromString("{3[m], 4[m], 6[m]}");
    Tensor coeffs = CoefficientList.of(zeros);
    assertEquals(coeffs, Tensors.fromString("{-72[m^3], 54[m^2], -13[m], 1}"));
    assertEquals(Polynomial.of(coeffs).apply(Quantity.of(3, "m")), Quantity.of(0, "m^3"));
    Chop._14.requireZero(Polynomial.of(coeffs).apply(Quantity.of(4, "m")));
    Chop._14.requireZero(Polynomial.of(coeffs).apply(Quantity.of(6, "m")));
    Tensor roots = Roots.of(coeffs);
    Chop._14.requireClose(roots, zeros);
  }

  @Test
  public void testEmptyFail() {
    assertThrows(IndexOutOfBoundsException.class, () -> CoefficientList.of(Tensors.empty()));
  }
}
