// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.chq.DeterminateScalarQ;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.win.DirichletWindow;

class SpectrogramArrayImplTest {
  @Test
  void testMathematicaUnits() {
    Tensor vector = TestHelper.signal().extract(0, 100).maps(s -> Quantity.of(s, "s"));
    Tensor tensor = SpectrogramArray.of(Fourier.FORWARD::transform, 10, 3, DirichletWindow.FUNCTION).apply(vector);
    boolean status = Flatten.scalars(tensor) //
        .allMatch(DeterminateScalarQ::of);
    assertTrue(status);
  }
}
