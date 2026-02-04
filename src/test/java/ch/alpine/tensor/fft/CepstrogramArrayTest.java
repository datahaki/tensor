// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.chq.DeterminateScalarQ;

class CepstrogramArrayTest {
  @Test
  void testMathematica() {
    Tensor tensor = CepstrogramArray.Real.apply(TestHelper.signal());
    boolean status = Flatten.scalars(tensor) //
        .allMatch(DeterminateScalarQ::of);
    assertTrue(status);
  }
}
