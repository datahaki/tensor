// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MatFileFormatTest {
  @Test
  public void testSimple() {
    // the mat file format is not supported
    assertEquals(MatFileFormat.NOT_SUPPORTED.ordinal(), 0);
  }
}
