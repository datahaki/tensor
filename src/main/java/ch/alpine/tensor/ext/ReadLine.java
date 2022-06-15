// code by jph
package ch.alpine.tensor.ext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Stream;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ReadLine.html">ReadLine</a> */
public enum ReadLine {
  ;
  /** Hint: even after completion of a terminal operation on the returned {@link Stream}
   * the given {@link InputStream} is not necessarily closed. Therefore, use
   * try-with-resources statement on input stream.
   * 
   * <p>Example:
   * <pre>
   * try (InputStream inputStream = new FileInputStream(file)) {
   * . ReadLine.of(inputStream).map(...).forEach(...)
   * }
   * </pre>
   * 
   * @param inputStream
   * @param charset
   * @return lines in given inputStream as stream of strings */
  public static Stream<String> of(InputStream inputStream, Charset charset) {
    // gjoel found that {@link Files#lines(Path)} was unsuitable on Windows
    return new BufferedReader(new InputStreamReader(inputStream, charset)).lines();
  }

  /** uses the {@link Charset#defaultCharset() default charset}
   * 
   * @param inputStream
   * @return lines in given inputStream as stream of strings */
  public static Stream<String> of(InputStream inputStream) {
    // gjoel found that {@link Files#lines(Path)} was unsuitable on Windows
    return new BufferedReader(new InputStreamReader(inputStream)).lines();
  }
}
