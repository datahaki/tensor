// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.win.WindowFunctions;

class PeriodogramArrayTest {
  @Test
  void testDefault() {
    Tensor tensor = PeriodogramArray.of(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1));
    Tensor result = Tensors.vector(0, 0, 2, 0, 0, 0, 2, 0);
    Tolerance.CHOP.requireClose(tensor, result);
  }

  @Test
  void testSize() {
    Tensor tensor = PeriodogramArray.of(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1), 4);
    Tensor result = Tensors.vector(0, 1, 0, 1); // confirmed with Mathematica
    Tolerance.CHOP.requireClose(tensor, result);
  }

  @Test
  void testSizeOffset() {
    Tensor tensor = PeriodogramArray.of(4, 1).apply(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1));
    Tensor result = Tensors.vector(0, 1, 0, 1); // confirmed with Mathematica
    Tolerance.CHOP.requireClose(tensor, result);
  }

  @ParameterizedTest
  @EnumSource(WindowFunctions.class)
  void testWindow(WindowFunctions windowFunctions) throws ClassNotFoundException, IOException {
    TensorUnaryOperator tuo = Serialization.copy(PeriodogramArray.of(4, 1, windowFunctions.get()));
    Tensor res = tuo.apply(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1));
    assertEquals(res.length(), 4);
  }

  @Test
  void testZeroFail() {
    assertThrows(IllegalArgumentException.class, () -> PeriodogramArray.of(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1), 0));
  }
}
