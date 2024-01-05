// code by jph
package ch.alpine.tensor.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ReadLine;
import ch.alpine.tensor.ext.ResourceData;

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
    case MATHEMATICA -> Get.of(inputStream);
    case VECTOR -> VectorFormat.parse(ReadLine.of(inputStream));
    // ---
    case CSV -> XsvFormat.CSV.parse(ReadLine.of(inputStream));
    case TSV -> XsvFormat.TSV.parse(ReadLine.of(inputStream));
    // ---
    case BMP, GIF, JPEG, JPG, PNG, TIF, TIFF -> ImageFormat.from(ImageIO.read(inputStream));
    // ---
    case MTX -> MatrixMarket.of(inputStream);
    default -> throw new UnsupportedOperationException(extension.name());
    };
  }
}
