// code by jph
package ch.alpine.tensor.itp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.HilbertMatrix;
import ch.alpine.tensor.qty.Quantity;

class BSplineFunctionTest {
  @Test
  void testIdentical() {
    Tensor control = HilbertMatrix.of(20, 3).map(scalar -> Quantity.of(scalar, "bsp"));
    Tensor domain = Subdivide.of(6, 14, 28);
    for (int degree = 0; degree <= 5; ++degree) {
      ScalarTensorFunction stfC = BSplineFunctionCyclic.of(3, control);
      ScalarTensorFunction stfS = BSplineFunctionString.of(3, control);
      Tensor tensor = domain.map(stfC);
      assertEquals(tensor, domain.map(stfS));
      ExactTensorQ.require(tensor);
    }
  }
}
