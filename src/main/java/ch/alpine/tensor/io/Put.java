// code by jph
package ch.alpine.tensor.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.stream.Collectors;

import ch.alpine.tensor.Tensor;

/** export of tensor to file, or a string compatible with Mathematica.
 * The output is similar to Object::toString and readable in any text editor.
 * 
 * <p>The file format is intended for data exchange between
 * Mathematica and the tensor library.
 * 
 * <p>file is readable in Mathematica where the file is
 * imported using Mathematica::Get.
 * 
 * <p>the format does not specify or require any particular
 * file extension. Mathematica also does not define an extension
 * for this format.
 * 
 * <p>Quote from Mathematica:
 * "Put by default uses the character encoding "PrintableASCII" when
 * creating a file."
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Put.html">Put</a> */
public enum Put {
  ;
  /** @param file destination of write
   * @param tensor
   * @throws IOException */
  public static void of(File file, Tensor tensor) throws IOException {
    Objects.requireNonNull(tensor);
    try (OutputStream outputStream = new FileOutputStream(file)) {
      of(outputStream, tensor);
    }
  }

  /** @param outputStream
   * @param tensor */
  public static void of(OutputStream outputStream, Tensor tensor) {
    ExportHelper.lines(MathematicaFormat.of(tensor), outputStream);
  }

  /** @param tensor
   * @return string expression of tensor compatible with Mathematica
   * @see Pretty#of(Tensor) */
  public static String string(Tensor tensor) {
    return MathematicaFormat.of(tensor).collect(Collectors.joining("\n"));
  }
}
