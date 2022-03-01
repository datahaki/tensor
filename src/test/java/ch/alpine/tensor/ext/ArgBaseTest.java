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
    assertEquals(ArgMin.of(Tensors.empty()), ArgBase.EMPTY);
    assertEquals(ArgMax.of(Tensors.empty()), ArgBase.EMPTY);
  }

  public void testMaxComparatorEmpty() {
    assertEquals(ArgBase.EMPTY, ArgMax.of(Tensors.empty(), Collections.reverseOrder()));
    assertEquals(ArgBase.EMPTY, ArgMin.of(Tensors.empty(), Collections.reverseOrder()));
  }
}
