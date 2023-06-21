// code by jph
package ch.alpine.tensor.sca;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.mat.Tolerance;

class SawtoothWaveTest {
  @Test
  void test() {
    Tolerance.CHOP.requireClose(SawtoothWave.FUNCTION.apply(RealScalar.of(+1.2)), RealScalar.of(0.2));
    Tolerance.CHOP.requireClose(SawtoothWave.FUNCTION.apply(RealScalar.of(-1.2)), RealScalar.of(0.8));
  }
}
