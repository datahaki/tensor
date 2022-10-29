// code by jph
package ch.alpine.tensor.num;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class AbsArgTest {
  @Test
  void test() {
    Tensor tensor = AbsArg.of(ComplexScalar.of(3, 4));
    Tolerance.CHOP.requireClose(tensor, Tensors.vector(5, 0.9272952180016122));
  }
}
