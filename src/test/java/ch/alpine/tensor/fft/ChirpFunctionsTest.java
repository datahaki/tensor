// code by jph
package ch.alpine.tensor.fft;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

class ChirpFunctionsTest {
  @ParameterizedTest
  @EnumSource
  void test(ChirpFunctions chirpFunctions) {
    ScalarUnaryOperator suo = chirpFunctions.of(400, 1000);
    suo.apply(RealScalar.of(3));
  }
}
