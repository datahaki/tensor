// code by jph
package ch.alpine.tensor.io;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ReadLine;

/** access to resource data in jar files, for instance,
 * the content included in the tensor library.
 * 
 * <p>Tensor resources provided by the tensor library include
 * <pre>
 * /ch/alpine/tensor/img/colorscheme/classic.csv
 * </pre>
 * 
 * <p>Properties provided by the tensor library include
 * <pre>
 * /ch/alpine/tensor/qty/si.properties
 * </pre>
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ResourceData.html">ResourceData</a>
 * 
 * @see Import */
public enum ResourceData {
  ;
  /** Example use:
   * Interpolation interpolation = LinearInterpolation.of(ResourceData.of("/colorscheme/classic.csv"));
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

  /** @param string as path to resource
   * @return imported object
   * @throws Exception if resource could not be loaded */
  public static <T> T object(String string) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      return ImportHelper.object(inputStream);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  /** @param string as path to resource
   * @return imported properties
   * @throws Exception if resource could not be loaded */
  public static Properties properties(String string, Charset charset) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      try (Reader reader = new InputStreamReader(inputStream, charset)) {
        return ImportHelper.properties(reader);
      }
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  /** @param string
   * @return instance of {@link Properties} from parsing resource in UTF-8 encoding
   * @throws Exception if resource could not be loaded */
  public static Properties properties(String string) {
    return properties(string, StaticHelper.CHARSET);
  }

  /** Hint: function bypasses conversion of image to tensor. When the
   * image is needed as a {@link Tensor}, rather use {@link #of(String)}
   * 
   * @param string as path to resource
   * @return imported image
   * @throws Exception if resource could not be loaded */
  public static BufferedImage bufferedImage(String string) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      return ImageIO.read(inputStream);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  /** Remark: the function returns a list instead of a stream, because a stream
   * of strings would leave the file open until the stream is processed.
   * 
   * @param string
   * @return list of lines in resource
   * @throws Exception if resource could not be loaded */
  public static List<String> lines(String string) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      return ReadLine.of(inputStream).collect(Collectors.toList());
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }
}
