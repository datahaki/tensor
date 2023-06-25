// code by jph
package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

class FileNameJoinTest {
  @Test
  void testSimple() {
    File file = FileNameJoin.of("ch", "alpine", "tensor", "ext");
    assertEquals(file, new File("ch" + File.separator + "alpine" + File.separator + "tensor" + File.separator + "ext"));
  }

  @Test
  void testSingle() {
    File file = FileNameJoin.of("ch");
    assertEquals(file, new File("ch"));
  }
}
