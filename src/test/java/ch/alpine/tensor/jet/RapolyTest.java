// code by jph
package ch.alpine.tensor.jet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.mat.re.Det;

class RapolyTest {
  @Test
  void testRapoly1() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {0, 0}}");
    matrix.set(Rapoly.of(Tensors.vector(0, -1)), 0, 0);
    matrix.set(Rapoly.of(Tensors.vector(0, -1)), 1, 1);
    Rapoly rapoly = (Rapoly) Det.withoutDivision(matrix);
    assertEquals(rapoly.polynomial().roots(), Array.zeros(2));
  }

  @Test
  void testRapoly2() {
    Tensor matrix = Tensors.fromString("{{0, 1}, {2, 0}}");
    matrix.set(Rapoly.of(Tensors.vector(3, -1)), 0, 0);
    matrix.set(Rapoly.of(Tensors.vector(4, -1)), 1, 1);
    Rapoly rapoly = (Rapoly) Det.withoutDivision(matrix);
    Tensor roots = rapoly.polynomial().roots();
    ExactTensorQ.require(roots);
    assertEquals(Tensors.vector(2, 5), roots);
  }
}
