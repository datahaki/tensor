// code by jph
package ch.alpine.tensor.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Flatten;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ArrayPlot;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImageFormat;

/** export of available {@link ColorDataGradients} to a single image */
/* package */ enum ColorDataGradientsDemo {
  ;
  private static final Scalar TFF = RealScalar.of(255);

  public static void main(String[] args) throws IOException {
    int spa = 0;
    int hei = 15 + spa;
    int sep = 142;
    Tensor array = Array.of(list -> RealScalar.of(list.get(1)), hei - spa, 256);
    Tensor image = Tensors.empty();
    Tensor white = Array.of(l -> TFF, hei - spa, sep, 4);
    for (ScalarTensorFunction cdf : ColorDataGradients.values()) {
      image.append(Join.of(1, ArrayPlot.of(array, cdf), white));
      image.append(Array.zeros(spa, 256 + sep, 4));
    }
    image = Flatten.of(image, 1);
    {
      BufferedImage bufferedImage = ImageFormat.of(image);
      Graphics2D graphics = bufferedImage.createGraphics();
      GraphicsUtil.setQualityHigh(graphics);
      int piy = -3 - spa;
      graphics.setColor(Color.BLACK);
      for (ColorDataGradients cdg : ColorDataGradients.values()) {
        piy += hei;
        String string = cdg.name();
        graphics.drawString(string, 256 + 2, piy);
      }
      image = ImageFormat.from(bufferedImage);
    }
    int half = image.length() / 2;
    Tensor top = image.extract(0, half);
    Tensor bot = image.extract(half, image.length());
    Tensor res = Join.of(1, top, bot);
    File file = HomeDirectory.Pictures(ColorDataGradients.class.getSimpleName() + ".png");
    Export.of(file, res);
  }
}
