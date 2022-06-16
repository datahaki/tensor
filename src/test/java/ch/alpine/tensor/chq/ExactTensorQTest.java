// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.TensorRuntimeException;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;

class ExactTensorQTest {
  @Test
  void testAll() {
    assertTrue(ExactTensorQ.of(RationalScalar.HALF));
    assertTrue(ExactTensorQ.of(Tensors.vector(1, 2, 3)));
    assertTrue(ExactTensorQ.of(HilbertMatrix.of(3, 2)));
    assertFalse(ExactTensorQ.of(Tensors.vector(1, 1, 1.)));
  }

  @Test
  void testRequireAll() {
    ExactTensorQ.require(Tensors.fromString("{{9/8, 3/2[s]}, 1/2+3/4*I}"));
    assertThrows(TensorRuntimeException.class, () -> ExactTensorQ.require(Tensors.vector(1, 2, 3, 0.7)));
  }
}
