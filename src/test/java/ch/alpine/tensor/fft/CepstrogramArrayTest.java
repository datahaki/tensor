// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.chq.DeterminateScalarQ;

class CepstrogramArrayTest {
  static List<SpectrogramArray> spectrogramArrays() {
    return Arrays.asList( //
        CepstrogramArray.POWER, //
        CepstrogramArray.REAL, //
        CepstrogramArray.REAL1 //
    );
  }

  @ParameterizedTest
  @MethodSource("spectrogramArrays")
  void testMathematica(SpectrogramArray spectrogramArray) {
    Tensor tensor = spectrogramArray.apply(TestHelper.signal());
    boolean status = Flatten.scalars(tensor) //
        .allMatch(DeterminateScalarQ::of);
    assertTrue(status);
  }
}
