// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.chq.DeterminateScalarQ;

class CepstrogramArrayTest {
  @ParameterizedTest
  @EnumSource
  void testMathematica(CepstrogramArray cepstrogramArray) {
    Tensor tensor = cepstrogramArray.apply(TestHelper.signal());
    boolean status = Flatten.scalars(tensor) //
        .allMatch(DeterminateScalarQ::of);
    assertTrue(status);
  }
}
