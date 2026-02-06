// code by jph
package ch.alpine.tensor.ext;

import java.nio.file.Path;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FileBaseName.html">FileBaseName</a>
 * 
 * @see FileExtension */
public enum FileBaseName {
  ;
  /** Example:
   * "/home/user/info.txt" returns "info"
   * "/home/user/info.txt.gz" returns "info.txt"
   * "/home/user/.git" returns ".git"
   * "/home/user/Documents" returns "Documents"
   * 
   * @param path
   * @return base name of given path name */
  public static String of(Path path) {
    String string = path.getFileName().toString();
    int index = string.lastIndexOf(FileExtension.DOT);
    return 0 < index //
        ? string.substring(0, index)
        : string;
  }
}
