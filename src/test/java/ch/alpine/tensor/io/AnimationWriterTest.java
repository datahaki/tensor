// code by jph
package ch.alpine.tensor.io;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.usr.TestFile;

public class AnimationWriterTest {
  @Test
  public void testColor() throws Exception {
    File file = TestFile.withExtension("gif");
    try (AnimationWriter animationWriter = new GifAnimationWriter(file, 100, TimeUnit.MILLISECONDS)) {
      animationWriter.write(Array.zeros(3, 4));
      animationWriter.write(Array.zeros(3, 4));
    }
    assertTrue(file.isFile());
    assertTrue(file.delete());
  }

  @Test
  public void testFailExtension() {
    try (AnimationWriter animationWriter = new GifAnimationWriter(null, 100, TimeUnit.MILLISECONDS)) { // extension unknown
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
