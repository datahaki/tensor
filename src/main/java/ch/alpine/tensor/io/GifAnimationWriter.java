// code by jph
package ch.alpine.tensor.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.AnimatedGifWriter;

/** Example:
 * <pre>
 * try (AnimationWriter animationWriter = new GifAnimationWriter(path, 100, TimeUnit.MILLISECONDS)) {
 * animationWriter.append(bufferedImage);
 * ...
 * }
 * </pre>
 * 
 * in Mathematica, animated gif sequences are created by Mathematica::Export */
public class GifAnimationWriter implements AnimationWriter {
  private final AnimatedGifWriter animatedGifWriter;

  /** @param path typically with extension "gif"
   * @param period
   * @param timeUnit
   * @return
   * @throws IOException */
  public GifAnimationWriter(Path path, int period, TimeUnit timeUnit) throws IOException {
    animatedGifWriter = //
        AnimatedGifWriter.of(path, Math.toIntExact(TimeUnit.MILLISECONDS.convert(period, timeUnit)), true);
  }

  @Override // from AnimationWriter
  public void write(BufferedImage bufferedImage) throws Exception {
    animatedGifWriter.write(bufferedImage);
  }

  @Override // from AnimationWriter
  public void write(Tensor tensor) throws Exception {
    write(ImageFormat.of(tensor));
  }

  @Override // from AutoCloseable
  public void close() throws IOException {
    animatedGifWriter.close();
  }
}
