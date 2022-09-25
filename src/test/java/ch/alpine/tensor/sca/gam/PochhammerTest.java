// code by jph
package ch.alpine.tensor.sca.gam;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.sca.Chop;

class PochhammerTest {
  @Test
  void testExact() {
    Scalar scalar = Pochhammer.of(10, 6);
    assertEquals(scalar, RealScalar.of(3603600));
  }

  @Test
  void testNumeric() {
    Chop._06.requireClose(Pochhammer.of(10.2, 6.3), RealScalar.of(9.097295875649229E6));
    Chop._06.requireClose(Pochhammer.of(3, 6.3), RealScalar.of(38517.77898184823));
    Chop._06.requireClose(Pochhammer.of(3.3, 4), RealScalar.of(473.80409999999995));
  }
}
