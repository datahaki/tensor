// code by jph
package ch.alpine.tensor.ext.ref;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ClassPathsTest {
  @Test
  void testSimple() {
    String expected = String.join(System.getProperty("path.separator"), "b", "asd");
    assertEquals(expected, ClassPaths.join("b", null, "asd"));
  }

  @Test
  void testResource() {
    assertNotNull(ClassPaths.getResource());
  }
}
