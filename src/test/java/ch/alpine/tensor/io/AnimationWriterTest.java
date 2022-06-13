// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ch.alpine.tensor.alg.Array;

class AnimationWriterTest {
  @Test
  void testColor(@TempDir File tempDir) throws Exception {
    File file = new File(tempDir, "file.gif");
    try (AnimationWriter animationWriter = new GifAnimationWriter(file, 100, TimeUnit.MILLISECONDS)) {
      animationWriter.write(Array.zeros(3, 4));
      animationWriter.write(Array.zeros(3, 4));
    }
    assertTrue(file.isFile());
  }

  @Test
  void testFailExtension() {
    try (AnimationWriter animationWriter = new GifAnimationWriter(null, 100, TimeUnit.MILLISECONDS)) { // extension unknown
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
