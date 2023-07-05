// code by jph
package ch.alpine.tensor.ext;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

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
 * <a href="https://reference.wolfram.com/language/ref/ResourceData.html">ResourceData</a> */
public enum ResourceData {
  ;
  /** @param string as path to resource
   * @return imported object
   * @throws Exception if resource could not be loaded */
  public static <T> T object(String string) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      return ObjectFormat.parse(inputStream);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  /** @param reader
   * @return
   * @throws IOException */
  public static Properties properties(Reader reader) throws IOException {
    Properties properties = new Properties();
    properties.load(reader);
    return properties;
  }

  /** @param string as path to resource
   * @return imported properties
   * @throws Exception if resource could not be loaded */
  public static Properties properties(String string, Charset charset) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      try (Reader reader = new InputStreamReader(inputStream, charset)) {
        return properties(reader);
      }
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  /** @param string
   * @return instance of {@link Properties} from parsing resource in UTF-8 encoding
   * @throws Exception if resource could not be loaded */
  public static Properties properties(String string) {
    return properties(string, StandardCharsets.UTF_8);
  }

  /** @param string as path to resource, typically starts with the slash character '/'
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
