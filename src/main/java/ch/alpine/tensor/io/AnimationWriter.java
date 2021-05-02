// code by jph
package ch.alpine.tensor.io;

import java.awt.image.BufferedImage;

import ch.alpine.tensor.Tensor;

/** compile sequence of images to animation */
public interface AnimationWriter extends AutoCloseable {
  /** @param bufferedImage to append to the image sequence of the animation
   * @throws Exception */
  void write(BufferedImage bufferedImage) throws Exception;

  /** @param tensor to append to the image sequence of the animation
   * @throws Exception */
  void write(Tensor tensor) throws Exception;
}
