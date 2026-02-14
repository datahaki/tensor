// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.rot.Cross;
import ch.alpine.tensor.mat.AntisymmetricMatrixQ;

class ZeroDefectSquareMatrixQTest {
  @Test
  void testCross() {
    assertTrue(AntisymmetricMatrixQ.INSTANCE.test(Cross.skew3(Tensors.vector(1, 2, 3))));
  }
}
