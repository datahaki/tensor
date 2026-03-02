// code by jph
package ch.alpine.tensor.num;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;

class ArithmeticGeometricMeanTest {
  @Test
  void test() {
    Scalar agm = ArithmeticGeometricMean.INSTANCE.apply(RealScalar.of(3), RealScalar.of(5));
    Tolerance.CHOP.requireClose(agm, RealScalar.of(3.9362355036495558));
  }
}
