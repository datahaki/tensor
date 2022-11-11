// code by jph
package ch.alpine.tensor.fft;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.SawtoothWave;

class CepstrogramArrayTest {
  private static Tensor signal() {
    return Tensor.of(IntStream.range(0, 10000) //
        .mapToObj(i -> RationalScalar.of(i, 100).add(RationalScalar.of(i * i, 1000_000))) //
        .map(SawtoothWave.INSTANCE));
  }

  @Test
  void testMathematica() {
    CepstrogramArray.of(signal());
  }

  @Test
  void testOperator() throws ClassNotFoundException, IOException {
    TensorUnaryOperator tuo = CepstrogramArray.of(1345, 300);
    Serialization.copy(tuo);
    tuo.apply(signal());
    assertTrue(tuo.toString().startsWith("CepstrogramArray["));
  }
}
