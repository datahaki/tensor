package ch.alpine.tensor.fft;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;

class ChirpFunctionsTest {
  @Test
  void test() {
    ScalarUnaryOperator suo = ChirpFunctions.linear(RealScalar.of(400), RealScalar.of(1000));
    suo.apply(RealScalar.of(3));
  }
}
