// code by jph
package ch.alpine.tensor.ext;

import java.nio.file.Path;
import java.util.Objects;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FileBaseName.html">FileBaseName</a>
 * <a href="https://reference.wolfram.com/language/ref/FileExtension.html">FileExtension</a>
 * 
 * @param path
 * @param parent may be null
 * @param title
 * @param extension */
public record PathName(Path path, Path parent, String title, String extension, boolean hasDot) {
  private static final char DOT = '.';

  public static PathName of(Path path) {
    Path parent = path.getParent();
    String string = path.getFileName().toString();
    int index = string.lastIndexOf(DOT);
    return 0 < index //
        ? new PathName(path, parent, string.substring(0, index), string.substring(index + 1), true)
        : new PathName(path, parent, string, "", false);
  }

  /** @return */
  public PathName truncate() {
    return of(parent.resolve(title));
  }

  /** @param string
   * @return */
  public Path withExtension(String string) {
    String concat = title + (string.isEmpty() ? "" : DOT + string);
    return Objects.isNull(parent) //
        ? Path.of(concat)
        : parent.resolve(concat);
  }

  /** @param string
   * @return */
  public boolean hasExtension(String string) {
    return string.equalsIgnoreCase(extension);
  }

  public String name() {
    return title + (hasDot ? "" : DOT + extension);
  }

  public Path asDirectory() {
    return parent.resolve(title);
  }
}
