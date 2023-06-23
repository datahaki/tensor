// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.mat.HilbertMatrix;

class DeterminateTensorQTest {
  @Test
  void test() {
    assertTrue(DeterminateTensorQ.of(LeviCivitaTensor.of(3)));
    DeterminateTensorQ.require(HilbertMatrix.of(3));
    Tensor tensor = Tensors.fromString("{2, NaN}");
    assertFalse(DeterminateTensorQ.of(tensor));
    assertThrows(Exception.class, () -> DeterminateTensorQ.require(tensor));
  }

  @Test
  void testInfty() {
    Tensor tensor = Tensors.fromString("{2, Infinity}");
    assertFalse(FiniteTensorQ.of(tensor));
    assertTrue(DeterminateTensorQ.of(tensor));
    DeterminateTensorQ.require(tensor);
  }
}
