// code by jph
package ch.alpine.tensor.usr;

import java.io.File;

import ch.alpine.tensor.ext.HomeDirectory;
import junit.framework.TestCase;

public class TestFileTest extends TestCase {
  public void testSimple() {
    File file = TestFile.withExtension("ethz");
    assertEquals(file, HomeDirectory.file("TestFileTest_testSimple.ethz"));
  }
}
