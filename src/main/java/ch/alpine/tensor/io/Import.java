// code by jph
package ch.alpine.tensor.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.DataFormatException;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ObjectFormat;
import ch.alpine.tensor.ext.ResourceData;

/** supported file formats are: CSV, GIF, JPG, PNG, etc.
 * 
 * <p>In order to import content from jar files use {@link ResourceData}.
 * 
 * <p>See also the documentation of {@link CsvFormat} regarding the decimal
 * format for numeric import using the tensor library.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Import.html">Import</a>
 * 
 * @see Export
 * @see ResourceData
 * @see Get */
public enum Import {
  ;
  // TODO TENSOR import GPX
  /** Supported extensions include
   * <ul>
   * <li>bmp for {@link ImageFormat}
   * <li>csv for comma separated values format
   * <li>jpg for {@link ImageFormat}
   * <li>png for {@link ImageFormat}
   * <li>tsv for tab separated values format
   * </ul>
   * 
   * @param file source
   * @return file content as {@link Tensor}
   * @throws IOException if file cannot be read */
  public static Tensor of(File file) throws IOException {
    try (InputStream inputStream = new FileInputStream(file)) {
      return ImportHelper.of(new Filename(file.getName()), inputStream);
    }
  }

  /** Example use:
   * Interpolation interpolation = LinearInterpolation.of(Import.of("/colorscheme/classic.csv"));
   * 
   * @param string as path to resource
   * @return imported tensor
   * @throws Exception if resource could not be loaded */
  public static Tensor of(String string) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      return ImportHelper.of(new Filename(string), inputStream);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  /** import function for Java objects that implement {@link Serializable}
   * and were stored with {@link Export#object(File, Object)}.
   * 
   * @param file
   * @return object prior to serialization, non-null
   * @throws IOException if file does not exist
   * @throws ClassNotFoundException
   * @throws DataFormatException */
  public static <T> T object(File file) //
      throws IOException, ClassNotFoundException, DataFormatException {
    return Objects.requireNonNull(ObjectFormat.parse(Files.readAllBytes(file.toPath())));
  }

  /** @param file in UTF-8 encoding
   * @return instance of {@link Properties} with key-value pairs specified in given file
   * @throws FileNotFoundException
   * @throws IOException */
  public static Properties properties(File file) throws FileNotFoundException, IOException {
    return properties(file, StaticHelper.CHARSET);
  }

  /** @param file
   * @param charset for instance StandardCharsets.UTF_8
   * @return instance of {@link Properties} with key-value pairs specified in given file
   * @throws FileNotFoundException
   * @throws IOException */
  public static Properties properties(File file, Charset charset) throws FileNotFoundException, IOException {
    try (Reader reader = new BufferedReader(new FileReader(file, charset))) {
      return ResourceData.properties(reader);
    }
  }
}
