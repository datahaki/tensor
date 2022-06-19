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
  /** As of Java 18, the default charset is UTF-8. */
  private static final Charset CHARSET = Charset.forName("UTF-8");

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

  /** uses the UTF-8 charset, which is the default charset as of Java 18
   * 
   * @param inputStream
   * @return lines in given inputStream as stream of strings */
  public static Stream<String> of(InputStream inputStream) {
    return of(inputStream, CHARSET);
  }
}
