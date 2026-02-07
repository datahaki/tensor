// code by jph
package ch.alpine.tensor.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ObjectFormat;
import ch.alpine.tensor.ext.PathName;

/** supported file formats are:
 * BMP, JPG, PNG (for images)
 * CSV, CSV.GZ, TSV, TSV.GZ (for matrices)
 * M (for arrays)
 * 
 * <p>Hint: Do not use Export when exchanging {@link Tensor}s with
 * Mathematica. For that purpose use {@link Put} and {@link Get}.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Export.html">Export</a>
 * 
 * @see Import
 * @see Put */
public enum Export {
  ;
  public static final ThreadLocal<Float> JPEG_QUALITY = ThreadLocal.withInitial(() -> 0.98f);

  /** See the documentation of {@link XsvFormat}, {@link ImageFormat}, and {@link MatlabExport}
   * for information on how tensors are encoded in the respective format.
   * 
   * <p>If the extension of the given file is not used in the tensor library, an exception
   * is thrown, and the file will not be created.
   * 
   * @param path destination
   * @param tensor non-null
   * @throws IOException if writing to the file fails
   * @throws IllegalArgumentException if extension of given file can not be associated
   * to a supported file format */
  public static void of(Path path, Tensor tensor) throws IOException {
    PathName pathName = PathName.of(path);
    _check(pathName);
    Objects.requireNonNull(tensor); // tensor non-null
    try (OutputStream outputStream = Files.newOutputStream(path)) {
      ExportHelper.of(pathName, tensor, outputStream);
    }
  }

  /** @param filename
   * @return
   * @throws Exception if sequence of file extensions is invalid */
  private static void _check(PathName filename) {
    Extension.of(filename.extension());
    while (filename.hasExtension(Extension.GZ.toString())) {
      filename = filename.truncate();
      Extension.of(filename.extension());
    }
  }

  /** export function for Java objects that implement {@link Serializable}.
   * To retrieve the object, use {@link Import#object(Path)}.
   * 
   * @param path
   * @param object non-null that implements {@link Serializable}
   * @throws IOException */
  public static void object(Path path, Object object) throws IOException {
    Files.write(path, ObjectFormat.of(Objects.requireNonNull(object)));
  }
}
