// code by jph
package ch.alpine.tensor.fft;

import java.util.stream.IntStream;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.SawtoothWave;

enum TestHelper {
  ;
  static Tensor signal() {
    return Tensor.of(IntStream.range(0, 10000) //
        .mapToObj(i -> Rational.of(i, 100).add(Rational.of(i * i, 1000_000))) //
        .map(SawtoothWave.FUNCTION));
  }
}
