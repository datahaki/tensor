// code by jph
package ch.alpine.tensor.io;

import java.lang.reflect.Modifier;

import ch.alpine.tensor.usr.AssertFail;
import junit.framework.TestCase;

public class ExtensionTest extends TestCase {
  public void testTruncate() {
    Filename filename = new Filename("dir/some.bmp.gz");
    assertEquals(filename.extension(), Extension.GZ);
    Filename truncate = filename.truncate();
    assertEquals(truncate.extension(), Extension.BMP);
  }

  public void testExtension() {
    Filename filename = new Filename("dir/some.gif");
    assertEquals(filename.extension(), Extension.GIF);
  }

  public void testSimple() {
    assertEquals(Extension.of("bMp"), Extension.BMP);
    assertEquals(Extension.of("gz"), Extension.GZ);
  }

  public void testFail() {
    AssertFail.of(() -> Extension.of("unknown"));
  }

  public void testVisibility() {
    assertFalse(Modifier.isPublic(Extension.class.getModifiers()));
  }
}
