// code by jph
package ch.alpine.tensor.ext;

import java.io.File;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FileNameJoin.html">FileNameJoin</a> */
public enum FileNameJoin {
  ;
  /** @param file
   * @param strings
   * @return */
  public static File of(File file, String... strings) {
    for (String string : strings)
      file = new File(file, string);
    return file;
  }

  /** @param string
   * @param strings
   * @return */
  public static File of(String string, String... strings) {
    return of(new File(string), strings);
  }
}
