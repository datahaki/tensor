// code by jph
package ch.alpine.tensor.ext;

import java.lang.reflect.Modifier;
import java.util.Collections;

import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class ArgBaseTest extends TestCase {
  public void testVisibility() {
    assertFalse(Modifier.isPublic(ArgBase.class.getModifiers()));
  }

  public void testEmpty2() {
    assertEquals(ArgMin.of(Tensors.empty()), ArgMin.EMPTY);
    assertEquals(ArgMax.of(Tensors.empty()), ArgMax.EMPTY);
  }

  public void testMaxComparatorEmpty() {
    assertEquals(ArgMin.EMPTY, ArgMax.of(Tensors.empty(), Collections.reverseOrder()));
    assertEquals(ArgMax.EMPTY, ArgMin.of(Tensors.empty(), Collections.reverseOrder()));
  }
}
