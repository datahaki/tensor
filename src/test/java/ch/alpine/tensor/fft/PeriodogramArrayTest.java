// code by jph
package ch.alpine.tensor.fft;

import java.io.IOException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.sca.win.WindowFunctions;
import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class PeriodogramArrayTest extends TestCase {
  public void testDefault() {
    Tensor tensor = PeriodogramArray.of(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1));
    Tensor result = Tensors.vector(0, 0, 2, 0, 0, 0, 2, 0);
    Tolerance.CHOP.requireClose(tensor, result);
  }

  public void testSize() {
    Tensor tensor = PeriodogramArray.of(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1), 4);
    Tensor result = Tensors.vector(0, 1, 0, 1); // confirmed with Mathematica
    Tolerance.CHOP.requireClose(tensor, result);
  }

  public void testSizeOffset() {
    Tensor tensor = PeriodogramArray.of(4, 1).apply(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1));
    Tensor result = Tensors.vector(0, 1, 0, 1); // confirmed with Mathematica
    Tolerance.CHOP.requireClose(tensor, result);
  }

  public void testWindow() throws ClassNotFoundException, IOException {
    for (WindowFunctions windowFunctions : WindowFunctions.values()) {
      TensorUnaryOperator tuo = Serialization.copy(PeriodogramArray.of(4, 1, windowFunctions.get()));
      Tensor res = tuo.apply(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1));
      assertEquals(res.length(), 4);
    }
  }

  public void testZeroFail() {
    AssertFail.of(() -> PeriodogramArray.of(Tensors.vector(0, 1, 0, -1, 0, 1, 0, -1), 0));
  }
}
