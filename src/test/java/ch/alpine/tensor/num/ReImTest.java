// code by jph
package ch.alpine.tensor.num;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.alg.UnitVector;

class ReImTest {
  @Test
  void testSimple() {
    assertEquals(ReIm.of(RealScalar.ONE), UnitVector.of(2, 0));
    assertEquals(ReIm.of(ComplexScalar.I), UnitVector.of(2, 1));
  }
}
