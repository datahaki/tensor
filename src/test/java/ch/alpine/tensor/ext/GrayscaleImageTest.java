package ch.alpine.tensor.ext;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.img.ColorFormat;
import ch.alpine.tensor.io.ImageFormat;

class GrayscaleImageTest {
  @Test
  void test() {
    BufferedImage bufferedImage = GrayscaleImage.of(2, 3);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(new Color(128, 200, 128, 128));
    graphics.drawLine(0, 0, 2, 0);
    graphics.dispose();
    Tensor tensor = ImageFormat.from(bufferedImage);
    // IO.println(tensor);
    Tensor rgba = tensor.get(0, 0);
    Color color = ColorFormat.toColor(rgba);
    assertEquals(color, new Color(182, 182, 182, 128));
  }
}
