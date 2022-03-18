// code by jph
package ch.alpine.tensor.usr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.HomeDirectory;

public class TestFileTest {
  @Test
  public void testSimple() {
    File file = TestFile.withExtension("ethz");
    assertEquals(file, HomeDirectory.file("TestFileTest_testSimple.ethz"));
  }
}
