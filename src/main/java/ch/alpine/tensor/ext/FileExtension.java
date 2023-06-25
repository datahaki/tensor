// code by jph
package ch.alpine.tensor.ext;

import java.io.File;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FileExtension.html">FileExtension</a> */
public enum FileExtension {
  ;
  private static final char DOT = '.';

  /** @param file
   * @return extension of given file name */
  public static String of(File file) {
    return fromName(file.getName());
  }

  /** @param string
   * @return extension of file name specified by given string */
  public static String of(String string) {
    return of(new File(string));
  }

  private static String fromName(String string) {
    int index = string.lastIndexOf(DOT);
    return 0 < index //
        ? string.substring(index + 1)
        : "";
  }
}
