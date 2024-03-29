// code by jph
package ch.alpine.tensor.usr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.PadLeft;
import ch.alpine.tensor.alg.PadRight;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.sca.Ceiling;

/* package */ enum ColorDataListsDemo {
  ;
  static String csv(String name) {
    if (name.charAt(0) == '_')
      name = name.substring(1);
    return name.toLowerCase();
  }

  public static void main(String[] args) throws IOException {
    Tensor image = Tensors.empty();
    for (ColorDataLists cdi : ColorDataLists.values()) {
      Tensor vector = Tensors.vector(i -> i < cdi.cyclic().length() ? RealScalar.of(i) : DoubleScalar.INDETERMINATE, 16);
      image.append(vector.map(cdi.cyclic()));
    }
    image = PadLeft.with(RealScalar.of(255), image.length(), 16 + 2, 4).apply(image);
    int ceil = Ceiling.FUNCTION.apply(RationalScalar.of(image.length(), 3)).multiply(RealScalar.of(3)).number().intValue();
    image = PadRight.with(RealScalar.of(0), ceil, 19, 4).apply(image);
    int spa = 0;
    int size = 12 + spa;
    Tensor large = ImageResize.nearest(image, size, size - spa);
    for (int count = size - spa; count < large.length(); count += size)
      for (int i = 0; i < spa; ++i)
        large.set(s -> RealScalar.ZERO, count + i, Tensor.ALL, Tensor.ALL);
    BufferedImage bufferedImage = ImageFormat.of(large);
    {
      Graphics2D graphics = bufferedImage.createGraphics();
      GraphicsUtil.setQualityHigh(graphics);
      graphics.setColor(Color.BLACK);
      int y = -1 - spa;
      for (ColorDataLists cdi : ColorDataLists.values()) {
        y += size;
        graphics.drawString(csv(cdi.name()), 0, y);
      }
      graphics.dispose();
    }
    large = ImageFormat.from(bufferedImage);
    int split = large.length() / 3;
    large = Join.of(1, large.extract(0, split), large.extract(split, 2 * split), large.extract(2 * split, 3 * split));
    Export.of(HomeDirectory.Pictures(ColorDataLists.class.getSimpleName().toLowerCase() + ".png"), large);
  }
}
