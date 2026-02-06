// code by jph
package ch.alpine.tensor.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ReadLine;
import ch.alpine.tensor.spa.SparseArray;

/** The file format is intended for data exchange between
 * Mathematica and the tensor library.
 * 
 * <p>Get imports an expression that was created by Mathematica::Put
 * <pre>
 * Put[expression, file, CharacterEncoding -> "UTF8"]
 * </pre>
 * as well as the tensor libraries' {@link Put}.
 * 
 * The format is similar to Object::toString and readable in any text editor.
 * 
 * <p>example content
 * <pre>
 * {{2 + 9*I, 3 - I, 3 - 2.423*I},
 * {(23 + I) / 4, 3.1415}, {}}
 * </pre>
 * 
 * <p>the format does not specify or require any particular
 * file extension. Mathematica also does not define an extension
 * for this format.
 * 
 * <p>Careful:
 * the import of {@link SparseArray} is not supported.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Get.html">Get</a> */
public enum Get {
  ;
  /** @param path source
   * @return
   * @throws IOException */
  public static Tensor of(Path path) throws IOException {
    try (InputStream inputStream = Files.newInputStream(path)) {
      return of(inputStream);
    }
  }

  /** @param inputStream source
   * @return */
  public static Tensor of(InputStream inputStream) {
    return MathematicaFormat.parse(ReadLine.of(inputStream));
  }
}
