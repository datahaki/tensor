// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class ExtensionTest {
  @Test
  public void testTruncate() {
    Filename filename = new Filename("dir/some.bmp.gz");
    assertEquals(filename.extension(), Extension.GZ);
    Filename truncate = filename.truncate();
    assertEquals(truncate.extension(), Extension.BMP);
  }

  @Test
  public void testExtension() {
    Filename filename = new Filename("dir/some.gif");
    assertEquals(filename.extension(), Extension.GIF);
  }

  @Test
  public void testSimple() {
    assertEquals(Extension.of("bMp"), Extension.BMP);
    assertEquals(Extension.of("gz"), Extension.GZ);
  }

  @Test
  public void testFail() {
    assertThrows(IllegalArgumentException.class, () -> Extension.of("unknown"));
  }

  @Test
  public void testVisibility() {
    assertFalse(Modifier.isPublic(Extension.class.getModifiers()));
  }
}
