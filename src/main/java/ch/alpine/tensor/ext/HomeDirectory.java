// code by jph
package ch.alpine.tensor.ext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/$HomeDirectory.html">$HomeDirectory</a> */
public enum HomeDirectory {
  /** Items shown on the desktop */
  Desktop,
  /** Default location for text files, Office docs, etc. */
  Documents,
  /** Where browsers save files by default */
  Downloads,
  /** Audio files */
  Music,
  /** Image files */
  Pictures,
  /** (possibly does not pre-exist on Windows) */
  Templates,
  /** Video files */
  Videos;

  private static final Path USER_HOME = Path.of(System.getProperty("user.home"));

  /** On linux, the directory has the form
   * /home/$USERNAME/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/string[0]/string[1]/... */
  public static Path path(String... strings) {
    return join(USER_HOME, strings);
  }

  /** On linux, the directory has the form
   * /home/$USERNAME/Desktop/string[0]/string[1]/...
   * 
   * @param strings
   * @return $user.home/Desktop/string[0]/string[1]/... */
  public Path resolve(String... strings) {
    Path path = path(name());
    if (!Files.isDirectory(path))
      try {
        IO.println("create directory " + path);
        Files.createDirectory(path);
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      }
    return join(path, strings);
  }

  private static Path join(Path start, String... parts) {
    return Arrays.stream(parts).reduce(start, Path::resolve, Path::resolve);
  }
}
