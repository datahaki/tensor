// code by jph
package ch.alpine.tensor.ext;

import java.io.File;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FileBaseName.html">FileBaseName</a> */
public enum FileBaseName {
  ;
  private static final char DOT = '.';

  /** Example:
   * "/home/user/info.txt" returns "info"
   * "/home/user/info.txt.gz" returns "info.txt"
   * 
   * @param file
   * @return base name of given file name */
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
        ? string.substring(0, index)
        : string;
  }
}
