// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class CoefficientListTest extends TestCase {
  public void testSimple() {
    Tensor zeros = Tensors.vector(3);
    Tensor coeffs = CoefficientList.of(zeros);
    Chop.NONE.requireZero(Series.of(coeffs).apply(RealScalar.of(3)));
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, zeros);
  }

  public void testQuantityD1() {
    Tensor zeros = Tensors.fromString("{3[m]}");
    Tensor coeffs = CoefficientList.of(zeros);
    // System.out.println(Series.of(coeffs).apply(Quantity.of(3, "m")));
    Scalar result = Series.of(coeffs).apply(Quantity.of(3, "m"));
    ExactScalarQ.require(result);
    assertEquals(result, Quantity.of(0, "m"));
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, zeros);
  }

  public void testQuantityD2() {
    Tensor zeros = Tensors.fromString("{3[m], 4[m]}");
    Tensor coeffs = CoefficientList.of(zeros);
    Chop.NONE.requireZero(Series.of(coeffs).apply(Quantity.of(3, "m")));
    Chop.NONE.requireZero(Series.of(coeffs).apply(Quantity.of(4, "m")));
    Tensor roots = Roots.of(coeffs);
    ExactTensorQ.require(roots);
    assertEquals(roots, zeros);
  }

  public void testQuantityD3() {
    Tensor zeros = Tensors.fromString("{3[m], 4[m], 6[m]}");
    Tensor coeffs = CoefficientList.of(zeros);
    assertEquals(Series.of(coeffs).apply(Quantity.of(3, "m")), Quantity.of(0, "m^3"));
    Chop._14.requireZero(Series.of(coeffs).apply(Quantity.of(4, "m")));
    Chop._14.requireZero(Series.of(coeffs).apply(Quantity.of(6, "m")));
    Tensor roots = Roots.of(coeffs);
    Chop._14.requireClose(roots, zeros);
  }

  public void testEmptyFail() {
    AssertFail.of(() -> CoefficientList.of(Tensors.empty()));
  }
}
