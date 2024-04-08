// adapted by jph
package ch.alpine.tensor.ext;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public enum Jpeg {
  ;
  /** Reference:
   * https://stackoverflow.com/questions/13204432/java-how-to-set-jpg-quality
   * 
   * @param bufferedImage
   * @param object
   * @param quality
   * @throws IOException */
  public static void put(BufferedImage bufferedImage, Object object, float quality) throws IOException {
    System.out.println("type=" + bufferedImage.getType());
    if (bufferedImage.getType() != BufferedImage.TYPE_3BYTE_BGR) {
      BufferedImage im = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
      Graphics2D graphics = im.createGraphics();
      graphics.drawImage(bufferedImage, 0, 0, null);
      graphics.dispose();
      bufferedImage = im;
    }
    try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(object)) {
      Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("jpeg");
      ImageWriter imageWriter = iterator.next();
      ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
      imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      imageWriteParam.setCompressionQuality(quality);
      imageWriter.setOutput(imageOutputStream);
      imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);
      imageWriter.dispose();
    }
  }
}
