// code by jph
package ch.alpine.tensor.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ObjectFormat;
import ch.alpine.tensor.ext.ReadLine;

/** functionality used in {@link Import} and {@link ResourceData} */
/* package */ enum ImportHelper {
  ;
  /** @param filename
   * @param inputStream
   * @return
   * @throws IOException */
  public static Tensor of(Filename filename, InputStream inputStream) throws IOException {
    Extension extension = filename.extension();
    if (extension.equals(Extension.GZ))
      try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
        return of(filename.truncate(), gzipInputStream);
      }
    return of(extension, inputStream);
  }

  private static Tensor of(Extension extension, InputStream inputStream) throws IOException {
    return switch (extension) {
    case CSV -> CsvFormat.parse(ReadLine.of(inputStream));
    case MATHEMATICA -> Get.of(inputStream);
    case BMP, GIF, JPG, PNG -> ImageFormat.from(ImageIO.read(inputStream));
    case VECTOR -> VectorFormat.parse(ReadLine.of(inputStream));
    default -> throw new UnsupportedOperationException(extension.name());
    };
  }

  /** @param inputStream
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws DataFormatException */
  public static <T> T object(InputStream inputStream) throws IOException, ClassNotFoundException, DataFormatException {
    int length = inputStream.available();
    byte[] bytes = new byte[length];
    inputStream.read(bytes);
    return ObjectFormat.parse(bytes);
  }

  /** @param inputStream
   * @return
   * @throws IOException */
  public static Properties properties(InputStream inputStream) throws IOException {
    Properties properties = new Properties();
    properties.load(inputStream);
    return properties;
  }
}
