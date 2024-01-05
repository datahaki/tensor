// code by jph
package ch.alpine.tensor.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Jpeg;

/* package */ enum ExportHelper {
  ;
  /** @param filename
   * @param tensor
   * @param outputStream
   * @throws IOException */
  public static void of(Filename filename, Tensor tensor, OutputStream outputStream) throws IOException {
    Extension extension = filename.extension();
    if (extension.equals(Extension.GZ))
      try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
        of(filename.truncate(), tensor, gzipOutputStream);
      }
    else
      of(extension, tensor, outputStream);
  }

  private static final float JPG_QUALITY = 0.98f;

  /** @param extension
   * @param tensor
   * @param outputStream
   * @throws IOException */
  public static void of(Extension extension, Tensor tensor, OutputStream outputStream) throws IOException {
    switch (extension) {
    case MATHEMATICA -> Put.of(outputStream, tensor);
    case VECTOR -> lines(VectorFormat.of(tensor), outputStream);
    // ---
    case CSV -> lines(XsvFormat.CSV.of(tensor), outputStream);
    case TSV -> lines(XsvFormat.TSV.of(tensor), outputStream);
    // --- images
    case BMP -> ImageIO.write(ImageFormat.bgr(tensor), extension.name(), outputStream);
    case JPEG, JPG -> Jpeg.put(ImageFormat.bgr(tensor), outputStream, JPG_QUALITY);
    case GIF, PNG, TIF, TIFF -> ImageIO.write(ImageFormat.of(tensor), extension.name(), outputStream);
    // ---
    case M -> lines(MatlabExport.of(tensor), outputStream);
    default -> throw new UnsupportedOperationException(extension.name());
    }
  }

  // the use of BufferedOutputStream is motivated by
  // http://www.oracle.com/technetwork/articles/javase/perftuning-137844.html
  public static void lines(Stream<String> stream, OutputStream outputStream) {
    try (PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, StaticHelper.CHARSET))) {
      stream.sequential().forEach(printWriter::println);
    } // writer close
  }
}
