// code by jph
package ch.alpine.tensor.ext;

import java.nio.file.Path;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FileExtension.html">FileExtension</a>
 * 
 * @see FileBaseName */
public enum FileExtension {
  ;
  public static final char DOT = '.';

  /** @param path
   * @return extension of given path name */
  public static String of(Path path) {
    return fromName(path.getFileName().toString());
  }

  /** @param string
   * @return extension of file name specified by given string */
  public static String of(String string) {
    return of(Path.of(string));
  }

  private static String fromName(String string) {
    int index = string.lastIndexOf(DOT);
    return 0 < index //
        ? string.substring(index + 1)
        : "";
  }
}
