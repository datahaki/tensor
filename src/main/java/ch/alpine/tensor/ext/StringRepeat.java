// code by jph
package ch.alpine.tensor.ext;

import java.util.stream.Stream;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/StringRepeat.html">StringRepeat</a> */
public enum StringRepeat {
  ;
  /** @param string
   * @param n non-negative
   * @return */
  public static String of(String string, int n) {
    StringBuilder stringBuilder = new StringBuilder(n * string.length());
    Stream.generate(() -> string).limit(n).forEach(stringBuilder::append);
    return stringBuilder.toString();
  }
}
