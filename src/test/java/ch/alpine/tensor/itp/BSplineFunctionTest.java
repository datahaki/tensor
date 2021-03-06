// code by jph
package ch.alpine.tensor.itp;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;
import junit.framework.TestCase;

public class BSplineFunctionTest extends TestCase {
  public void testIdentical() {
    Tensor control = HilbertMatrix.of(20, 3).map(scalar -> Quantity.of(scalar, "bsp"));
    Tensor domain = Subdivide.of(6, 14, 28);
    for (int degree = 0; degree <= 5; ++degree) {
      ScalarTensorFunction stfC = BSplineFunction.cyclic(3, control);
      ScalarTensorFunction stfS = BSplineFunction.string(3, control);
      Tensor tensor = domain.map(stfC);
      assertEquals(tensor, domain.map(stfS));
      ExactTensorQ.require(tensor);
    }
  }
}
