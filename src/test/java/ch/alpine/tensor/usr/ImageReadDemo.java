// code by jph
package ch.alpine.tensor.usr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.Thumbnail;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.Import;

enum ImageReadDemo {
  ;
  public static void main(String[] args) throws IOException {
    File file = HomeDirectory.file("testimage", "COPY_P1150723_a.JPG");
    switch (2) {
    case 1: {
      BufferedImage bufferedImage = ImageIO.read(file);
      System.out.println(bufferedImage);
      Tensor tensor = ImageFormat.from(bufferedImage);
      Tensor thumb = Thumbnail.of(tensor, 100);
      Export.of(HomeDirectory.file("testimage", "thumb.jpg"), thumb);
      break;
    }
    case 2: {
      Tensor tensor2 = Import.of(file);
      Tensor thumb = Thumbnail.of(tensor2, 200);
      Export.of(HomeDirectory.file("testimage", "thumb2.jpg"), thumb);
      break;
    }
    }
  }
}
