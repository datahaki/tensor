// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

class ArgBaseTest {
  @Test
  public void testVisibility() {
    assertFalse(Modifier.isPublic(ArgBase.class.getModifiers()));
  }

  @Test
  public void testEmpty2() {
    assertEquals(ArgMin.of(Tensors.empty()), ArgBase.EMPTY);
    assertEquals(ArgMax.of(Tensors.empty()), ArgBase.EMPTY);
  }

  @Test
  public void testMaxComparatorEmpty() {
    assertEquals(ArgBase.EMPTY, ArgMax.of(Tensors.empty(), Collections.reverseOrder()));
    assertEquals(ArgBase.EMPTY, ArgMin.of(Tensors.empty(), Collections.reverseOrder()));
  }
}
