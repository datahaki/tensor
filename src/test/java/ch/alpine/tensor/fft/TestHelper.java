// code by jph
package ch.alpine.tensor.fft;

import java.util.stream.IntStream;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.SawtoothWave;

enum TestHelper {
  ;
  static Tensor signal() {
    return Tensor.of(IntStream.range(0, 10000) //
        .mapToObj(i -> RationalScalar.of(i, 100).add(RationalScalar.of(i * i, 1000_000))) //
        .map(SawtoothWave.FUNCTION));
  }
}
