// code by jph
package ch.alpine.tensor.sca.bes;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;

class BesselITest {
  @Test
  void test() {
    Scalar scalar = BesselI._0(1.2);
    Tolerance.CHOP.requireClose(scalar, RealScalar.of(1.393725584134064));
  }
}
