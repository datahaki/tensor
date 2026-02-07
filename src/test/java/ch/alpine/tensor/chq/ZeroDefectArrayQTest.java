// code by jph
package ch.alpine.tensor.chq;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;

class ZeroDefectArrayQTest {
  @Test
  void testFail() {
    assertThrows(Exception.class, () -> new ZeroDefectArrayQ(0, null) {
      @Override
      public Tensor defect(Tensor tensor) {
        return null;
      }
    });
    assertThrows(Exception.class, () -> new ZeroDefectArrayQ(-1, null) {
      @Override
      public Tensor defect(Tensor tensor) {
        return null;
      }
    });
  }
}
