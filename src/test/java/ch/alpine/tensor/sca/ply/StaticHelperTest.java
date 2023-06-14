// code by jph
package ch.alpine.tensor.sca.ply;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Unit;

class StaticHelperTest {
  @Test
  void test() {
    Unit unit = StaticHelper.getDomainUnit(Tensors.fromString("{2[m],3[m*s^-1]}"));
    assertEquals(unit, Unit.of("s"));
  }
}
