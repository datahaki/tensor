// adapted by jph
package ch.alpine.tensor.ext;

import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public enum Jpeg {
  ;
  private static final Set<Integer> TYPES = Set.of( //
      BufferedImage.TYPE_BYTE_GRAY, //
      BufferedImage.TYPE_INT_BGR, // TODO TENSOR not sure
      BufferedImage.TYPE_3BYTE_BGR);

  /** Reference:
   * https://stackoverflow.com/questions/13204432/java-how-to-set-jpg-quality
   * 
   * @param bufferedImage
   * @param object e.g. instance of File
   * @param quality between 0 and 1
   * @throws IOException */
  public static void put(BufferedImage bufferedImage, Object object, float quality) throws IOException {
    if (TYPES.contains(bufferedImage.getType()))
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
    else {
      // if (true) {
      ColorConvertOp op = new ColorConvertOp(null);
      BufferedImage target = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
      op.filter(bufferedImage, target);
      put(target, object, quality);
      // } else {
      // BufferedImage converted = new BufferedImage( //
      // bufferedImage.getWidth(), //
      // bufferedImage.getHeight(), //
      // BufferedImage.TYPE_3BYTE_BGR);
      // Graphics2D graphics = converted.createGraphics();
      // graphics.drawImage(bufferedImage, 0, 0, null);
      // graphics.dispose();
      // put(converted, object, quality);
      // }
    }
    // put(new AffineTransformOp(new AffineTransform(), AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter( //
    // bufferedImage, //
    // new BufferedImage( //
    // bufferedImage.getWidth(), //
    // bufferedImage.getHeight(), //
    // BufferedImage.TYPE_3BYTE_BGR)),
    // object, quality);
  }
}
