// code by jph
package ch.alpine.tensor.ext;

import java.nio.file.Path;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FileBaseName.html">FileBaseName</a>
 * <a href="https://reference.wolfram.com/language/ref/FileExtension.html">FileExtension</a>
 * 
 * @param parent may be null
 * @param title
 * @param extension */
public record PathName(Path parent, String title, String extension) {
  private static final char DOT = '.';

  public static PathName of(Path path) {
    return of(path.getParent(), path.getFileName().toString());
  }

  public static PathName of(Path parent, String string) {
    int index = string.lastIndexOf(DOT);
    return 0 < index //
        ? new PathName(parent, string.substring(0, index), string.substring(index + 1))
        : new PathName(parent, string, "");
  }

  public PathName truncate() {
    return of(parent, title);
  }
}
