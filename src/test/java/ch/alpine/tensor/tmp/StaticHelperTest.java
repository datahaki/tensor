// code by jph
package ch.alpine.tensor.tmp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

class StaticHelperTest {
  @Test
  void testScalar() {
    assertEquals(StaticHelper.COPY_SECOND.apply(Pi.VALUE, RealScalar.ZERO), RealScalar.ZERO);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> StaticHelper.COPY_SECOND.apply(Tensors.vector(1, 2), Tensors.vector(1, 2, 3)));
  }

  @Test
  void testVisibility() {
    assertFalse(Modifier.isPublic(StaticHelper.class.getModifiers()));
  }
}
